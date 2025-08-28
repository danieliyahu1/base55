package com.akatsuki.base55.domain.workflow;

import java.util.Map;

public record WorkflowStepRequest(String origianlTask, String stepDescription,
                                  Map<String, String> previousStepResultDependencies) {}