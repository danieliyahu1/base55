package com.akatsuki.base55.dto;

public record PlannerRequestDTO(
        String originalTask,
        String currentState
) {
}