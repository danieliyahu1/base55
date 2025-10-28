package com.akatsuki.base55.domain.agent;

import com.akatsuki.base55.enums.TaskOutcome;

public record LlmEvaluationResult(String reason, TaskOutcome result) {
}