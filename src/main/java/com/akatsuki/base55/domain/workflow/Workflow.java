package com.akatsuki.base55.domain.workflow;

import com.akatsuki.base55.domain.workflow.step.WorkflowStep;

import java.util.List;

public record Workflow(String analysis, List<WorkflowStep> WorkflowSteps) {}
