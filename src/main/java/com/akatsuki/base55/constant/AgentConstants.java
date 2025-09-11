package com.akatsuki.base55.constant;

public class AgentConstants {

    // --- task decomposition ---

    public static final String SYSTEM_PROMPT_TASK_DECOMPOSER = """
        You are an expert task decomposer AI.
        The step you decompose will be executed by another LLM agent.
        Therefore, generate concise, precise, and actionable instructions that an AI can follow directly.
        Only propose the single decompose step based on completed work; do not plan multiple future steps.
        """;

    public static final String USER_PROMPT_DECOMPOSE_TASK = """
        Overall task: {task}
        current state of the agent: {currentState}
        
        Decompose the task into a single decompose step that the LLM can execute.
        Output only one clear and actionable instruction.
        """;

    public static final String FIRST_DECOMPOSITION = """
        The agent is at the initial stage and requires a clear, executable instruction to begin the task.
    """;
}
