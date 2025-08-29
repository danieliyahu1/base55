package com.akatsuki.base55.domain.workflow.step;

import java.util.Map;

public record WorkflowStepRequest(String origianlTask, String stepDescription,
                                  Map<String, String> previousStepResultDependencies) {}