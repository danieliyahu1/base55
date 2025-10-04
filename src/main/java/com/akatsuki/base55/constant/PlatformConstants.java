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
           - id
           - A concise description of the task the agent must perform
           - A list of conceptual data required by this step (requiredData). This is not actual data, just what the step depends on.
    
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

    public static final String TOOL_FILTERING_LLM_ROLE = "You are an expert at analyzing workflows and selecting the most relevant tools to accomplish each step.";

    public static final String TOOL_FILTERING_TASK_DESCRIPTION = """
    For the following workflow step, analyze all available tools and decide which ones are most relevant. Provide a structured response with the following information for each relevant tool:
    - The tool's ID.
    - A brief explanation of why the tool is a good fit for the step.
    - The specific function or purpose of the tool that matches the step's requirements.
    
    Step: {step}
    
    Available tools:
    
    {tools}
    """;


    // --- vector db / similarity search ---

    public static final int SIMILARITY_SEARCH_TOP_K = 50;
    public static final String SIMILARITY_SEARCH_THRESHOLD = "0.7";
}
