package com.akatsuki.base55.domain.workflow;

import java.util.List;
import java.util.Map;

public record WorkflowStep(String id, String description, List<String> previousStepResultDependencies,
                           Map<String, String> nextSteps) { }