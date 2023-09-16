package com.mawardi.iot.monitoring.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mawardi.iot.monitoring.model.SensorDetail;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Data
public class StoreDataService {

    private List<SensorDetail> storedSensorDetails;
    private ObjectMapper objectMapper;
    @PostConstruct
    public void postConstruct() throws IOException {
        objectMapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/local-data-store.json");
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: local-data-store.json");
        }
        storedSensorDetails = objectMapper.readValue(inputStream, new TypeReference<>() {});
    }

    public boolean storeSensorDetail(SensorDetail newData) {
        try {
            log.info("Starting data storage.");
            storedSensorDetails.add(newData);
            writeToFile();
            log.info("Finished data storage.");
            return true;
        } catch (IOException e) {
            log.error("Exception Occurred: Could not write new Sensor Detail to Json file.");
            return false;
        }
    }

    public boolean clearStoredSensorDetails() {
        storedSensorDetails = new ArrayList<>();
        try {
            writeToFile();
            return true;
        } catch (IOException e) {
            log.error("Exception Occurred: failed to clear data store");
            return false;
        }

    }

    private void writeToFile() throws IOException {
        OutputStream outputStream = new FileOutputStream("src/main/resources/local-data-store.json");
        ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
        writer.writeValue(outputStream, storedSensorDetails);
    }

}
