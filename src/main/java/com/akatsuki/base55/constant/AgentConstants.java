package com.akatsuki.base55.constant;

public class AgentConstants {

    // --- task decomposition ---

    public static final String SYSTEM_PROMPT_TASK_DECOMPOSER = """
        You are an expert task decomposer AI.
        The step you decompose will be executed by another LLM agent.
        Therefore, generate concise, precise, and actionable instructions that an AI can follow directly.
        Only propose the single decompose step based on completed work; do not plan multiple future steps.
        """;

    public static final String NEXT_SUB_TASK_TASK = """
        Based on the last sub-task completed, decompose the user original task into a single decompose step that the LLM can execute.
        Output only one clear and actionable instruction.
        last completed sub-task: {sub-task}
        """;

    public static final String FIRST_DECOMPOSITION = """
        The agent is at the initial stage and requires a clear, executable sub-task for LLM to begin the task.
    """;


    // --- sub-task execution ---

    public static final String SYSTEM_PROMPT_SUB_TASK_EXECUTOR = """
        You are a reliable expert AI agent that executes specific sub-tasks.
        Carefully read the sub-task description and determine the best approach to accomplish it.
        If needed use the available tools effectively to gather information, perform actions, and achieve the desired outcome.
        Always ensure that your actions align with the sub-task requirements.
        """;

    public static final String USER_PROMPT_SUB_TASK_EXECUTOR = """
        Execute reliably the following sub-task:
        {sub-task}
        """;
}
