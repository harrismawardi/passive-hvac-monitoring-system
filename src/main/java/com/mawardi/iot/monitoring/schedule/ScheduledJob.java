package com.mawardi.iot.monitoring.schedule;

import com.mawardi.iot.monitoring.model.FeedbackRequest;
import com.mawardi.iot.monitoring.model.SensorDetail;
import com.mawardi.iot.monitoring.service.StoreDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ScheduledJob {
    @Autowired
    private StoreDataService dataStorage;

    @Value("${trigger.thresholds.windspeed}")
    private double windSpeedThreshold;

    @Value("${trigger.thresholds.humidity}")
    private double humidityThreshold;

    @Value("${trigger.thresholds.particulate}")
    private double particulateThreshold;

    @Value("${hvac.controller.url}")
    private String hvacControllerUrl;

    @Scheduled(cron = "*/10 * * * * *")
    public void evaluateReceivedSensorReadings() {
        log.info("Starting scheduled job");
        try {
            List<SensorDetail> readings = dataStorage.getStoredSensorDetails();
            dataStorage.clearStoredSensorDetails();
            if (!readings.isEmpty()) {
                SensorDetail smoothedSensorDetail = SensorDetail.builder()
                        .humidity(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getHumidity).toArray()))
                        .windSpeed(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getWindSpeed).toArray()))
                        .particulate(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getParticulate).toArray()))
                        .build();

                FeedbackRequest feedback =  generateConditionFeedback(smoothedSensorDetail);
                makeFeedbackRequest(feedback);
            }
            dataStorage.clearStoredSensorDetails();
        } catch(Exception ex) {
            log.error(ex.getMessage() + ex.getCause());
        }
    }

    private Double smoothDataFluctuation(double[] sensorReadings) {
        try {
            // Using Exponential Moving Average to smooth data fluctuations
            // EMA = ((1 - Smoothing Factor) x previous EMA) + (Smoothing Factor x new Data point)
            double smoothingFactor = 0.5;
            double ema = sensorReadings[0];
            for (double reading : sensorReadings) {
                ema = (1 - smoothingFactor) * ema + smoothingFactor * reading;
            }
            return ema;
        } catch (Exception e){
            throw new RuntimeException("Exception Occurred: Failed to smooth data.", e);
        }
    }

    private FeedbackRequest generateConditionFeedback(SensorDetail detail) {
        //todo set the actions to the action object
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        Map<String, String> actionDetail = new HashMap<>();
        if (detail.getHumidity() < humidityThreshold) {
            actionDetail.put("humidity", "Increase ventilation");
        }
        if (detail.getWindSpeed() < windSpeedThreshold) {
            actionDetail.put("fan", "Increase fan speed to 90%");
        }
        if (detail.getParticulate() < particulateThreshold) {
            actionDetail.put("filtration", "Improve air filtration");
        }
        feedbackRequest.setActionNeeded(!actionDetail.isEmpty());
        feedbackRequest.setActionDetail(actionDetail);
        return feedbackRequest;
    }

    private void makeFeedbackRequest(FeedbackRequest feedback) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(hvacControllerUrl, feedback, String.class);
            log.info("Feedback Request made successfully. Response Body: {}", responseEntity.getBody());
        } catch (HttpClientErrorException ex) {
            log.error("Feedback Request received error response. Status Code: {}. Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
        }
    }

}