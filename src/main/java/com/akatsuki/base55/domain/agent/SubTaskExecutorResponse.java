package com.akatsuki.base55.domain.agent;

import java.util.Map;

public record SubTaskExecutorResponse(
        String executionReport,
        Map<String, String> data) {}