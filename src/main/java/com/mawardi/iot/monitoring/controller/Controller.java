package com.mawardi.iot.monitoring.controller;

import com.mawardi.iot.monitoring.model.SensorDetail;
import com.mawardi.iot.monitoring.service.StoreDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {
    @Autowired
    private StoreDataService cacheService;

    @PostMapping("/monitor")
    public ResponseEntity<String> receiveSensorReading(@RequestBody SensorDetail sensorDetail) {
        log.info("Received new Sensor Detail");
            if (cacheService.storeSensorDetail(sensorDetail)) {
                return ResponseEntity.accepted().body("Sensor detail received successfully.");
            } else {
                throw new RuntimeException("Failed to store sensor detail.");
            }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e) {
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}