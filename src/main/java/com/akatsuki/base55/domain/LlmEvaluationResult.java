package com.akatsuki.base55.domain;

import com.akatsuki.base55.enums.AgentFlowControl;

public record LlmEvaluationResult(String reason, AgentFlowControl result) {
}