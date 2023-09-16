package com.mawardi.iot.monitoring.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude
public class SensorDetail {
    //todo add validation annotation
    private String id;
    @JsonProperty("wind_speed") private double windSpeed;
    @JsonProperty("humidity") private double humidity;
    @JsonProperty("particulate") private double particulate;
}
