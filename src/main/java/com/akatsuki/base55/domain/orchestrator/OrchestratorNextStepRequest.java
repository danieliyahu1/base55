package com.akatsuki.base55.domain.orchestrator;

import java.util.Map;

public record OrchestratorNextStepRequest(String originalTask, String description, String stepResult, Map<String,String> candidates) {}