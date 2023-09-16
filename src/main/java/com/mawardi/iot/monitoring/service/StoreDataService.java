package com.mawardi.iot.monitoring.service;

import com.mawardi.iot.monitoring.model.SensorDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StoreDataService {

    public boolean storeSensorDetail(SensorDetail data) {
        return true;
    }

    public List<SensorDetail> getStoredSensorDetails() {
        return new ArrayList<>();
    }
}
