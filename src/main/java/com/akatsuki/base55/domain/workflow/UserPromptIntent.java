package com.akatsuki.base55.domain.workflow;

import java.util.List;

public record UserPromptIntent (
        String primaryGoal,
        List<String> secondaryGoals
) {}