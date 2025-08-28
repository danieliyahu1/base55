package com.akatsuki.base55.domain.workflow;

import java.util.List;

public record Workflow(String analysis, List<WorkflowStep> WorkflowSteps) {}
