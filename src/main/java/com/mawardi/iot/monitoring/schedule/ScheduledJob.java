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

    @Scheduled(cron = "0 0 15 * * ?")
    public void evaluateReceivedSensorReadings() {
        List<SensorDetail> readings = dataStorage.getStoredSensorDetails();

        SensorDetail smoothedSensorDetail = SensorDetail.builder()
                .humidity(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getHumidity).toArray()))
                .windSpeed(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getWindSpeed).toArray()))
                .particulate(smoothDataFluctuation(readings.stream().mapToDouble(SensorDetail::getParticulate).toArray()))
                .build();

        FeedbackRequest feedback =  generateConditionFeedback(smoothedSensorDetail);
        try {
            makeFeedbackRequest(feedback);
        } catch(Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException("Failed to action feedback Response", ex);
        }
    }

    private double smoothDataFluctuation(double[] sensorReadings) {
        // Using Exponential Moving Average to smooth data fluctuations
        // EMA = ((1 - Smoothing Factor) x previous EMA) + (Smoothing Factor x new Data point)
        double smoothingFactor = 0.5;
        double ema = sensorReadings[0];
        for (double reading : sensorReadings) {
            ema = (1 - smoothingFactor) * ema + smoothingFactor * reading;
        }
        return ema;
    }

    private FeedbackRequest generateConditionFeedback(SensorDetail detail) {
        //todo set the actions to the action object
        FeedbackRequest feedbackRequest = new FeedbackRequest();
        Map<String, String> actionDetail = new HashMap<>();
        if (detail.getHumidity() < humidityThreshold) {
            actionDetail.put("", "");
        }
        if (detail.getWindSpeed() < windSpeedThreshold) {
            actionDetail.put("", "");
        }
        if (detail.getParticulate() < particulateThreshold) {
            actionDetail.put("", "");
        }
        feedbackRequest.setActionNeeded(!actionDetail.isEmpty());
        feedbackRequest.setActionDetail(actionDetail);
        return feedbackRequest;
    }

    private void makeFeedbackRequest(FeedbackRequest feedback) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(hvacControllerUrl, feedback, String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                log.info("Feedback Request made successfully. Response Status Code: {}. Response Body: {}",
                        responseEntity.getStatusCode().value(), responseEntity.getBody());
            } else {
                log.error("Feedback Request received error response. Response Body");
            }
        } catch (HttpClientErrorException ex) {
            log.error("this is logged instead");
        }
    }

}