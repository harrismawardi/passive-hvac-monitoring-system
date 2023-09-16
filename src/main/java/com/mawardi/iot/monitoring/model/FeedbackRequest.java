package com.mawardi.iot.monitoring.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class FeedbackRequest {
    private boolean actionNeeded;
    private Map<String, String> actionDetail;
}
