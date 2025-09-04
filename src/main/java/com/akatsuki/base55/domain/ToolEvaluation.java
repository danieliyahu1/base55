package com.akatsuki.base55.domain;

public record ToolEvaluation(
        String uuid,          // tool UUID
        boolean isRequired,   // whether LLM thinks this tool is needed
        String rationale      // why the LLM thinks the tool is needed
) { }