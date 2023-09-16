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
    @JsonInclude private String id;
    @JsonProperty("wind_speed") private Double windSpeed;
    @JsonProperty("humidity") private Double humidity;
    @JsonProperty("particulate") private Double particulate;
}
