package com.akatsuki.base55.service;

import com.akatsuki.base55.domain.LlmEvaluationResult;

public interface LlmEvaluation {
    LlmEvaluationResult evaluate(String evaluationPrompt, String responseToEvaluate);
    
}
