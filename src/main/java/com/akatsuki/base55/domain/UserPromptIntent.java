package com.akatsuki.base55.domain;

import java.util.List;

public record UserPromptIntent (
        String primaryGoal,
        List<String> secondaryGoals
) {}