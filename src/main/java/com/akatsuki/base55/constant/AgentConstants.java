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
        last sub-task: {sub-task}
        lass sub-task evaluation: {evaluation}
        """;

    public static final String FIRST_DECOMPOSITION = """
        The agent is at the initial stage and requires a clear, executable sub-task for LLM to begin the task.
    """;


    // --- sub-task execution ---

    public static final String SYSTEM_PROMPT_SUB_TASK_EXECUTOR = """
        You are a reliable {ai_agent} that executes a task.
        Carefully read the sub-task description and determine the best approach to accomplish it.
        If needed use the available tools effectively to gather information, perform actions, and achieve the desired outcome.
        Always ensure that your actions align with the sub-task requirements.

         --- OUTPUT FORMAT MANDATE ---
        {
            "executionReport": A detailed explanation of the LLM's call process and the steps taken to accomplish the sub-task.
            "data": Structured JSON with the actual useful information extracted from the execution process.
        }
        
        FIELD MEANING:
        - descriptionOfSubTaskExecution:
        - data:
        """;

    public static final String SYSTEM_PROMPT_TASK_EXECUTOR = """
        You are a reliable and self verify {ai_agent} that can break a task to sub tasks and use multiple tools until the task is accomplished.
        Carefully read the task description and determine the best approach to accomplish it.
        """;


    public static final String USER_PROMPT_SUB_TASK_EXECUTOR = """
        {task}
        """;


    // --- sub-task evaluation ---

    public static final String SYSTEM_PROMPT_SUB_TASK_EVALUATOR = """
            You are the Evaluation Component of an AI Agent system.
             Your task is to evaluate the outcome of a single subtask execution and decide the next action.

             Your output must be a JSON object with the following fields:
               reason: explanation of your decision,
               result: PROCEED_TO_NEXT_STEP | RETRY_CURRENT_STEP | TASK_COMPLETED

             Here are the definitions of the possible results:
                - TASK_VERIFICATION: the agent wants to ask the user question regatding the task.
                - RETRY_TASK: The task response is not good enough or wrong.
                - TASK_COMPLETED: The task is now complete and the user has the final answer.
            
            """;

    public static final String USER_PROMPT_TASK_EVALUATOR = """
            Evaluate the following task execution response.

                Original Task:
                {task}
        
                LLM Response:
                {llm-response}
        """;
}
