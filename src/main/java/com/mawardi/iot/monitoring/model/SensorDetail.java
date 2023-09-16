package com.mawardi.iot.monitoring.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDetail {
    //todo add validation annotation
    private String id;
    private double windSpeed;
    private double humidity;
    private double particulate;
}
