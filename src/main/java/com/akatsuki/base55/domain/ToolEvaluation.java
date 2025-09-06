package com.akatsuki.base55.domain;

import java.util.UUID;

public record ToolEvaluation(
        UUID id,          // tool UUID
        boolean isRequired,   // whether LLM thinks this tool is needed
        String rationale      // why the LLM thinks the tool is needed
) { }