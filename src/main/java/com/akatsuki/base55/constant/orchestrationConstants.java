package com.akatsuki.base55.constant;

public class orchestrationConstants {

    public static final String WORKFLOW_PROMPT = """
    You are a system designed to generate a sequential workflow to accomplish a task.
    Your output must be a single JSON object.

    The JSON object must contain two fields:
    1. 'analysis': A string describing the overall plan for the workflow.
    2. 'WorkflowSteps': A list of 'WorkflowStep' objects, representing the sequential, successful path.

    Here is the definition of a 'WorkflowStep' object:
    - id: String (A unique identifier for the step)
    - description: String (A brief description of what the step does)
    - previousStepResultDependencies: List<String> (A list of 'id's of previous steps whose results are required as input for this step)
    - nextSteps: Map<String, String> (A map where the key is the next step's 'id' and the value is the condition to transition to that step. The conditions can be any meaningful string that describes the outcome of the step. If a step has no next step in the successful path, the map should be empty.)

    Generate a workflow for the following task:
    Task: <task>

    Return the output in the specified JSON format, without any additional text or explanation.

    Example of expected output format:
    <\\{
        "analysis": "This workflow finds and reserves a flight for a user. It searches for flights first, and then uses the flight details to make a reservation.",
        "WorkflowSteps": [
            <\\{
                "id": "step1_search_flights",
                "description": "Find available flights based on the user's criteria (origin, destination, dates).",
                "previousStepResultDependencies": [],
                "nextSteps": <\\{
                    "step2_check_availability": "flights_found"
                \\}>
            \\}>,
            ...
        ]
    \\}>
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
        <workflowStepRequest>
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
        4. Respond ONLY in JSON with the following fields:
           <\\{
             "id": "<id of chosen next step>",
             "reason": "<short explanation of why this step was chosen>"
           \\}>
        5. Do NOT include any extra text, explanation, or formatting.
        
        ---
        
        **Example:**
        Input Object:
        <\\{
          "originalTask": "Find and reserve a flight for a user",
          "description": "Search for available flights",
          "stepResult": "flights found",
          "candidates": <\\{
            "step2_check_availability": "flights_found",
            "step2_error_handling": "no_flights"
          \\}>
        \\}>
        
        Response:
        <\\{
          "id": "step2_check_availability",
          "reason": "The step result indicates flights were found, matching the condition for this step."
        \\}>
        """;
}
