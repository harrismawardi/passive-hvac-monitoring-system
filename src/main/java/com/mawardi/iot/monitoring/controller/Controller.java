package com.mawardi.iot.monitoring.controller;

import com.mawardi.iot.monitoring.model.SensorDetail;
import com.mawardi.iot.monitoring.service.StoreDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {
    @Autowired
    private StoreDataService cacheService;

    @PostMapping("/monitor")
    public ResponseEntity<String> receiveSensorReading(SensorDetail sensorDetail) {
        if (cacheService.storeSensorDetail(sensorDetail)) {
            return ResponseEntity.accepted().body("Sensor detail received successfully.");
        } else {
            return ResponseEntity.internalServerError().body("Failed to store sensor detail.");
        }

    }
}

       //todo
        /*

        Questions:
        - How does the service receive data
            - let's go with an API and then could refactor to an event broker?
        - Reactive programming?
        - Does the sensor send an accumulation of data from a period or a single reading?
        - Is this asyncronous? e.g. the response or non-response a response to the api call or call another endpoint asyncronous

        1.  aggregate sensor detail
        2.  smooth the data
        3.  evaluate is within threshold
        4.  trigger response
                1. warning message
                2. adjust louvre
                3. activate active ventilation

        * */