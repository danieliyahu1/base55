package com.akatsuki.base55.domain.workflow.step;

import java.util.List;
import java.util.Map;

public record WorkflowStep(String id, String task, List<String> requiredData) { }