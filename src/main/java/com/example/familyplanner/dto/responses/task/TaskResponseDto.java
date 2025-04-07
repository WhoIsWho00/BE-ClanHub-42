package com.example.familyplanner.dto.responses.task;

import com.example.familyplanner.dto.responses.UserResponseDto;
import com.example.familyplanner.entity.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Task data transfer object")
public class TaskResponseDto {
    @Schema(description = "Task unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Task title", example = "Buy groceries", required = true)
    private String title;

    @Schema(description = "Task description", example = "We need milk, eggs, and bread.")
    private String description;

    @Schema(description = "Task due date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Schema(description = "Task creation date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdAt;

    @Schema(description = "Task completion status", example = "false")
    private boolean completed;

    @Schema(description = "Task status", example = "IN_PROGRESS")
    private TaskStatus status;

    @Schema(description = "User assigned to the task")
    private UserResponseDto assignedTo;

    @Schema(description = "User who created the task")
    private UserResponseDto createdBy;

    @Schema(description = "Family ID the task belongs to")
    private UUID familyId;

    @Schema(description = "Task priority (1-5, where 5 is highest)", example = "3")
    private Integer priority;

    @Schema(description = "")
    private LocalDate completionDate;
}