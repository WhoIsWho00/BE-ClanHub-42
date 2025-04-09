package com.example.familyplanner.dto.requests.task;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request model for update task details")
public class UpdateTaskDetailsRequest {
    @Schema(description = "Title of the task", example = "Buy groceries")
    @Size(min = 1, max = 20)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Detailed description of the task", example = "Buy milk, bread, and eggs from the store")
    @Size(min = 0, max = 100)
    private String description;

    @Schema(description = "Due date for the task in format YYYY-MM-DD", example = "2025-03-31")
    @NotNull(message = "Due date is required")
    private LocalDate dueDate;




}
