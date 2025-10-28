package com.akatsuki.base55.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformConstants {

    // --- user intent parsing ---

    public static final String USER_INTENT_ROLE = "You are an expert at extracting user needs from their prompt, even when their words are unclear or don't fully express their true intent.";

    public static final String USER_INTENT_TASK_DESCRIPTION = """
        Extract the primary goal of the task (what outcome the agent must achieve) and secondary goals that the AI agent should have in order to be able to execute the task.
        Task: {task}
        """;


    // --- workflow generation ---

    public static final String WORKFLOW_GENERATION_ROLE = "You are an AI assistant that helps design agent workflows.";

    public static final String WORKFLOW_GENERATION_PROMPT = """
        Your task is to break this goal into a high-level workflow consisting of discrete steps that an AI agent should perform to accomplish the different tasks the user will want.
        The output must contain those fields:
        1. analysis: Describing the overall plan for the workflow.
        2. aiAgentDescription: summary for the LLM of what the agent role(ignore how it is done).
        3. systemPrompt: Describing the system prompt that the LLM should use for its reasoning/execution.
        4. WorkflowSteps: High-level workflow consisting of discrete steps that an AI agent should perform to accomplish the different tasks the user will want.
    
        Here is the definition of a 'WorkflowStep' object:
           - A concise description of the sub-task the agent must perform
    
        Notes:
            - Focus on high-level tasks only, do not generate execution details.
            - Consider optional steps if applicable.
            - Use meaningful descriptions and data dependency hints for tool selection.
    
        The user has the following goal:
        {primaryGoal}

        And the following secondary goals:
        {secondaryGoals}
    """;


    // --- tool filtering --

    public static final String TOOL_FILTERING_LLM_ROLE = """
    You are an expert at analyzing workflow and evaluating tools relevance to accomplish each step.
    For the following workflow step, analyze all available tools and provide a structured json that contain only a list with the evaluating results for each tool how relevant it is to accomplish the step.:
    
    "id": "UUID" // The tool spec id field,
    "isRequired": true or false,
    "rationale": "string" // A brief explanation of why the tool is required or not required
    """;

    public static final String TOOL_FILTERING_TASK_DESCRIPTION = """
    Step: {step}
    
    Available tools:
    
    {tools}
    """;


    // --- vector db / similarity search ---

    public static final int SIMILARITY_SEARCH_TOP_K = 25;
    public static final String SIMILARITY_SEARCH_THRESHOLD = "0.7";
}
