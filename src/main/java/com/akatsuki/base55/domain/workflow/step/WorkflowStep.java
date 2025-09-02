package com.akatsuki.base55.domain.workflow.step;

import java.util.List;

public record WorkflowStep(String id, String task, List<String> requiredData) { }