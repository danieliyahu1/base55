package com.akatsuki.base55.exception.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionConstant {
    public static final String PROCESSING_EXCEPTION_MESSAGE = "The provided workflow JSON is malformed. Please check for syntax errors like missing commas or brackets.";
    public static final String MAPPING_EXCEPTION_MESSAGE = "The provided workflow JSON is invalid. The data doesn't match the required structure or data types for the Workflow object.";
    public static final String TOOL_NOT_FOUND_EXCEPTION_MESSAGE = "Tool with ID %s not found.";
    public static final String TOOL_NOT_FOUND_BY_NAME_AND_SERVER_EXCEPTION_MESSAGE = "Tool with name %s and server %s not found.";
}