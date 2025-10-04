package com.akatsuki.base55.domain.agent;

import java.util.Map;

public record SubTaskExecutorResponse(
        String status,
        String descriptionOfSubTaskExecution,
        Map<String, String> data) {}