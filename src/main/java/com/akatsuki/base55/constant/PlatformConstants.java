package com.akatsuki.base55.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformConstants {

    public static final String WORKFLOW_PROMPT = """
    You are a system designed to generate a high level workflow of ai agent to accomplish a task.
    The output must contain three fields:
    1. 'analysis': A string describing the overall plan for the workflow.
    2. 'aiAgentDescription': summary for the LLM of what the agent role(ignore how it is done).
    3. 'WorkflowSteps': A list of 'WorkflowStep' objects, representing the sequential, successful paths, similar to a tree with one root and each leaf is a result that came from different way, you decide the number of leaf based on the task.

    Here is the definition of a 'WorkflowStep' object:
    - id: String (A unique identifier for the step)
    - description: String (A brief description of what the step does)
    - previousStepResultDependencies: List<String> (A list of 'id's of previous steps whose results are required as input for this step)
    - nextSteps: Map<String, String> (A map where the key is the next step's 'id' and the value is the condition to transition to that step. The conditions can be any meaningful string that describes the outcome of the step. If a step has no next step in the successful path, the map should be empty.)

    Generate a workflow for the following task:
    Task: {task}
    """;

    public static final String WORKFLOW_STEP_PROMPT = """
    You are a system designed to fulfill a single, well-defined step within a larger workflow.
    Your task is to process the provided information and generate a concise reply.

    ---
    **Input Object Schema (WorkflowStepRequest):**
    - originalTask: String
        The original goal of the workflow.
    - stepDescription: String
        A short description of the current step to fulfill.
    - dependenciesResults: Map<String, String>
        Key = dependency step id, Value = result of that step.
          This map may be empty if this step does not depend on any previous steps.

    ---
    **Actual Input Object:**
    {workflowStepRequest}
    """;

    public static final String DETERMINE_NEXT_STEP_PROMPT = """
    You are a workflow orchestrator AI.

    Your job is to select the next step to execute in a workflow based on the information provided in an input object.

    ---

    **Input Object Schema (OrchestratorNextStepRequest):**
    - originalTask: String — The overall goal of the workflow.
    - description: String — Description of the current step to fulfill.
    - stepResult: String — The result of completing the current step.
    - candidates: Map<String, String> — Key = next step id, Value = condition describing when this step should be executed.

    ---

    **Instructions:**
    1. Analyze the stepResult in the context of the current step description and the originalTask.
    2. Compare it against the conditions for each candidate next step.
    3. Pick the step that is most relevant based on the stepResult and the candidate conditions.
    
    ---
    """;

    // --- workflow generation ---

    public static final String GENERATE_WORKFLOW_PROMPT = """  
        Your task is to break this goal into a high-level workflow consisting of discrete steps that an AI agent should perform to accomplish the different tasks the user will want.
        For each step, provide:
        - id
        - A concise description of the task the agent must perform
        - A list of conceptual data required by this step (requiredData). This is not actual data, just what the step depends on.

        Additionally, provide a high-level analysis of the workflow, explaining how the steps work together to achieve the user's goal.

        Optional: Include multiple alternative steps if there are different ways to accomplish the same task.

        Notes:
        - Focus on high-level tasks only, do not generate execution details.
        - Consider optional steps if applicable.
        - Use meaningful descriptions and data dependency hints for tool selection.
        
        The user has the following goal:
        {primaryGoal}

        And the following secondary goals:
        {secondaryGoals}
        """;

    public static final String WORKFLOW_LLM_ROLE = "You are an AI assistant that helps design agent workflows.";

    // --- user intent parsing ---

    public static final String USER_INTENT_ROLE = "You are an expert at extracting user needs from their prompt, even when their words are unclear or don't fully express their true intent.";

    public static final String USER_INTENT_TASK_DESCRIPTION = """
        Extract the primary goal of the task (what outcome the agent must achieve) and secondary goals that the AI agent should have in order to be able to execute the task.
        Task: {task}
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

    public static final String EVALUATION_PROMPT = """
            User's original goal: {prompt}
            Text to evaluate: {result}
            """;

    public static final String EVALUATION_SYSTEM_ROLE = """
            You are an evaluation agent for an automated system. Your task is to review a given response and verify that it is correct and meets the expected criteria.

            Your response MUST be a single JSON object. Do not include any other text, explanations, or markdown formatting outside of the JSON object itself.

            The JSON object must have two fields:
            - `result`: A boolean value (`true` or `false`) indicating if the text meets the criteria.
            - `reason`: A string explaining the reason for the evaluation outcome. The reason should be concise and direct.

            ### Criteria to Evaluate:
            The text must be factually accurate and relevant to the user's query.
            """;

    public static final int SIMILARITY_SEARCH_TOP_K = 50;
    public static final String SIMILARITY_SEARCH_THRESHOLD = "0.7";
}
