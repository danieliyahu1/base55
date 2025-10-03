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
        1. 'analysis': A string describing the overall plan for the workflow.
        2. 'aiAgentDescription': summary for the LLM of what the agent role(ignore how it is done).
        3. 'systemPrompt': A string describing the system prompt that the LLM should use for its reasoning/execution.
        4. 'WorkflowSteps': A list of 'WorkflowStep' objects, representing the sequential, successful paths, similar to a tree with one root and each leaf is a result that came from different way, you decide the number of leaf based on the task.
    
        Here is the definition of a 'WorkflowStep' object:
        - id: String (A unique identifier for the step)
        - description: String (A brief description of what the step does)
        - previousStepResultDependencies: List<String> (A list of 'id's of previous steps whose results are required as input for this step)
        - nextSteps: Map<String, String> (A map where the key is the next step's 'id' and the value is the condition to transition to that step. The conditions can be any meaningful string that describes the outcome of the step. If a step has no next step in the successful path, the map should be empty.)
    
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
