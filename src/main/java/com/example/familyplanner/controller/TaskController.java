package com.example.familyplanner.controller;

import com.example.familyplanner.dto.requests.task.TaskRequest;
import com.example.familyplanner.dto.requests.task.UpdateTaskDetailsRequest;
import com.example.familyplanner.dto.responses.task.TaskResponseDto;
import com.example.familyplanner.dto.responses.task.TaskResponseInCalendarDto;
import com.example.familyplanner.entity.Task;
import com.example.familyplanner.entity.TaskStatus;
import com.example.familyplanner.entity.User;
import com.example.familyplanner.repository.TaskRepository;
import com.example.familyplanner.repository.UserRepository;
import com.example.familyplanner.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management API")
    public class TaskController {

        private final TaskService taskService;
        private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Operation(
            summary = "Get tasks for calendar",
            description = "Retrieves a list of tasks sorted from date of start to deadline",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Tasks successfully retrieved"
                                            }
                                            """
                            ))),

                    @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                        {
                          "timestamp": "2025-03-25T16:26:19.597Z",
                          "status": 401,
                          "error": "Unauthorized",
                          "message": "Authentication required. Please log in.",
                          "path": "/api/tasks"
                        }
                        """

                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                        {
                          "timestamp": "2025-03-25T16:26:19.597Z",
                          "status": 403,
                          "error": "Forbidden",
                          "message": "You do not have permission to access these tasks.",
                          "path": "/api/tasks"
                        }
                        """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "",
                                              "path": "/api/tasks"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/tasks"
                                            }
                                            """
                            )))
            }
    )
        @GetMapping("/calendar")
    // Возвращать только выполненые таски
        public ResponseEntity<List<TaskResponseInCalendarDto>> getTasksByDateRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                   Principal principal) {

        if(principal == null)
        {return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}

        String email = principal.getName();

        List<TaskResponseInCalendarDto> tasksBetweenDates = taskService.getTasksBetweenDates(startDate, endDate, email);
            return ResponseEntity.ok(tasksBetweenDates);

        }

    @Operation(summary = "Get List of Tasks",
            description = "Allows you to get List of Tasks for one specific user with all needed information",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                {
                                 "message": "Task successfully received"
                                }
                                """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                               {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 401,
                                                 "error": "Unauthorized",
                                                 "message": "Authentication required. Please log in.",
                                                 "path": "/api/tasks/list"
                                               }
                                               """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                "status": 403,
                                                 "error": "Forbidden",
                                                 "message": "You do not have permission to access these tasks.",
                                                 "path": "/api/tasks/list"
                                               }
                                               """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                              {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                 "status": 404,
                                                 "error": "Not Found",
                                                  "message": "",
                                                  "path": "/api/tasks/list"
                                                }
                                              """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 500,
                                                 "error": "Internal Server Error",
                                                 "message": "An unexpected error occurred.",
                                                 "path": "/api/tasks/list"
                                               }
                                               """
                            )))
            }
    )
    @GetMapping("/list")
    public ResponseEntity<List<TaskResponseDto>> getMyTasks(Principal principal) {

        if(principal == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = principal.getName();

        List<TaskResponseDto> tasks = taskService.getTasksForUser(email);
        return ResponseEntity.ok(tasks);
    }

//

//    @Operation(
//            summary = "Get tasks for calendar based on completion date for completed tasks",
//            description = "Retrieves a list of tasks where incomplete tasks are filtered by due date and completed tasks by completion date",
//            security = @SecurityRequirement(name = "JWT"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successful operation"),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)"),
//                    @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)"),
//                    @ApiResponse(responseCode = "404", description = "Not Found"),
//                    @ApiResponse(responseCode = "500", description = "Internal server error")
//            }
//    )
//    @GetMapping("/calendar/smart")
//    public ResponseEntity<List<TaskResponseInCalendarDto>> getTasksInDateRange(
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
//            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
//            Principal principal) {
//
//        if(principal == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        String email = principal.getName();
//        List<TaskResponseInCalendarDto> tasksInDateRange = taskService.getTasksInDateRange(startDate, endDate, email);
//        return ResponseEntity.ok(tasksInDateRange);
//    }
      
//    @Operation(
//            summary = "Get tasks with pagination and filtering",
//            description = "Retrieves a paginated list of tasks with optional filtering",
//            security = @SecurityRequirement(name = "JWT"),
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Successful operation",
//                            content = @Content(mediaType = "application/json",
//                                    schema = @Schema(implementation = Page.class))),
//                    @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)",
//                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
//                                    value = """
//                        {
//                          "timestamp": "2025-03-25T16:26:19.597Z",
//                          "status": 401,
//                          "error": "Unauthorized",
//                          "message": "Authentication required. Please log in.",
//                          "path": "/api/tasks"
//                        }
//                        """
//
//                            ))),
//                    @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
//                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
//                                    value = """
//                        {
//                          "timestamp": "2025-03-25T16:26:19.597Z",
//                          "status": 403,
//                          "error": "Forbidden",
//                          "message": "You do not have permission to access these tasks.",
//                          "path": "/api/tasks"
//                        }
//                        """
//                            ))),
//                    @ApiResponse(responseCode = "404", description = "Not Found",
//                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
//                                    value = """
//                                            {
//                                              "timestamp": "2025-03-25T16:26:19.597Z",
//                                              "status": 404,
//                                              "error": "Not Found",
//                                              "message": "",
//                                              "path": "/api/tasks"
//                                            }
//                                            """
//                            ))),
//                    @ApiResponse(responseCode = "500", description = "Internal server error",
//                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
//                                    value = """
//                                            {
//                                              "timestamp": "2025-03-25T16:26:19.597Z",
//                                              "status": 500,
//                                              "error": "Internal Server Error",
//                                              "message": "An unexpected error occurred.",
//                                              "path": "/api/tasks"
//                                            }
//                                            """
//                            )))
//            }
//    )
//    @GetMapping
////    вернуть список задач. Искать их по id
//    public ResponseEntity<Page<TaskResponseDto>> getTasks(
//            @Parameter(description = "Filter by family ID") @RequestParam(required = false) UUID familyId,
//            @Parameter(description = "Filter by completion status") @RequestParam(required = false) Boolean completed,
//            @Parameter(description = "Filter by assigned user ID") @RequestParam(required = false) UUID assignedTo,
//            @Parameter(description = "Filter by priority level (1-5)") @RequestParam(required = false) Integer priority,
//            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
//            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
//            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
//            @Parameter(description = "Sort direction (ASC or DESC)") @RequestParam(defaultValue = "DESC") String direction) {
//
//        Page<TaskResponseDto> tasks = taskService.getTasks(
//                familyId, completed, assignedTo, priority, page, size, sortBy, direction);
//
//        return ResponseEntity.ok(tasks);
//    }

    @Operation(
            summary = "Get task by ID",
            description = "Retrieves a specific task by its ID",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "message": "Task successfully found"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Task validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 401,
                                              "error": "Bad Request",
                                              "message": {
                                              "title": "title is required"
                                              },
                                              "path": "/api/tasks/2"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "",
                                              "path": "/api/tasks/2"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/tasks"
                                            }
                                            """
                            )))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @Parameter(description = "Task ID", required = true)
            @PathVariable UUID id) {

        TaskResponseDto task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(
            summary = "Create a new task for the authenticated user",
            description = "Creates a new task and associates it with the currently authenticated user. The task will be saved to the database.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully created",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Task successfully created"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "400", description = "Task validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 400,
                                              "error": "Bad Request",
                                              "message": {
                                              "title": "title is required"
                                              },
                                              "path": "/api/tasks/"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "403", description = "Forbidden (access denied)",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 403,
                                              "error": "Forbidden",
                                              "message": "Access denied. Insufficient permissions.",
                                              "path": "/api/tasks"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/tasks"
                                            }
                                            """
                            )))

            }
    )
    @PostMapping
    public ResponseEntity<String> createTask(
            Principal principal,
            @Valid @RequestBody TaskRequest request) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = principal.getName();
        taskService.createTask(request, email);

        return ResponseEntity.ok("Task created successfully");
    }
    @Operation(
            summary = "Update task status",
            description = "Updates the status(completed/not completed) of a specific task",
            security = @SecurityRequirement(name = "JWT"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Task successfully updated",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                                "message": "Task successfully updated"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "401", description = "Task validation failed",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 401,
                                              "error": "Bad Request",
                                              "message": {
                                              "title": "title is required"
                                              },
                                              "path": "/api/tasks/2"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "404", description = "Not Found",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 404,
                                              "error": "Not Found",
                                              "message": "",
                                              "path": "/api/tasks/2"
                                            }
                                            """
                            ))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                    value = """
                                            {
                                              "timestamp": "2025-03-25T16:26:19.597Z",
                                              "status": 500,
                                              "error": "Internal Server Error",
                                              "message": "An unexpected error occurred.",
                                              "path": "/api/tasks"
                                            }
                                            """
                            )))
            }
    )
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDto> updateTaskStatus(
            @Parameter(description = "Task ID", required = true) @PathVariable UUID id,
            @Parameter(description = "New status", required = true) @RequestParam TaskStatus status){

    TaskResponseDto updatedTask = taskService.updateTaskStatus(id, status);
    return ResponseEntity.ok(updatedTask);
}

       @Operation(
               summary = "Delete Task",
              description = "Delete Task by it's unique id. Access only for authorized User",
                security = @SecurityRequirement(name = "JWT"),
               responses = {
                       @ApiResponse(responseCode = "200", description = "Successful operation",
                               content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                       value = """
                                            {
                                                "message": "Task successfully deleted"
                                            }
                                            """
                              ))),
                        @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)",
                                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                       value = """
                                               {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 401,
                                                 "error": "Unauthorized",
                                                 "message": "Authentication required. Please log in.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                              ))),
                       @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
                               content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                       value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                "status": 403,
                                                 "error": "Forbidden",
                                                 "message": "You do not have permission to access these tasks.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                                ))),
                       @ApiResponse(responseCode = "404", description = "Not Found",
                              content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                        value = """
                                              {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                 "status": 404,
                                                 "error": "Not Found",
                                                  "message": "",
                                                  "path": "/api/tasks"
                                                }
                                              """
                               ))),
                       @ApiResponse(responseCode = "500", description = "Internal server error",
                             content = @Content(mediaType = "application/json", examples = @ExampleObject(
                                       value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 500,
                                                 "error": "Internal Server Error",
                                                 "message": "An unexpected error occurred.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                                )))
                }
       )
       @DeleteMapping
        public ResponseEntity<String> deleteTaskById (@RequestParam UUID id, Principal principal){
           Task task = taskRepository.findById(id)
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

           User currentUser = userRepository.findByEmail(principal.getName())
                   .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

           if (!task.getCreatedBy().equals(currentUser)) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this task");
           }

            taskService.deleteTask(id);

            return ResponseEntity.ok("Task deleted successfully");
        }


        @Operation(summary = "Update Task",
                description = "Update Task details by it's unique id. Access only for authorized User",
                security = @SecurityRequirement(name = "JWT"),
                responses = {
        @ApiResponse(responseCode = "200", description = "Successful operation",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                        value = """
                                {
                                 "message": "Task successfully updated"
                                }
                                """
                ))),
        @ApiResponse(responseCode = "401", description = "Unauthorized (authentication required)",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                        value = """
                                               {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 401,
                                                 "error": "Unauthorized",
                                                 "message": "Authentication required. Please log in.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                ))),
        @ApiResponse(responseCode = "403", description = "Forbidden (insufficient permissions)",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                        value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                "status": 403,
                                                 "error": "Forbidden",
                                                 "message": "You do not have permission to access these tasks.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                ))),
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                        value = """
                                              {
                                                  "timestamp": "2025-03-25T16:26:19.597Z",
                                                 "status": 404,
                                                 "error": "Not Found",
                                                  "message": "",
                                                  "path": "/api/tasks"
                                                }
                                              """
                ))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(mediaType = "application/json", examples = @ExampleObject(
                        value = """
                                               {
                                                 "timestamp": "2025-03-25T16:26:19.597Z",
                                                  "status": 500,
                                                 "error": "Internal Server Error",
                                                 "message": "An unexpected error occurred.",
                                                 "path": "/api/tasks"
                                               }
                                               """
                )))
        }
        )
        @PutMapping
        public ResponseEntity<TaskResponseDto> updateTaskDetails(@RequestParam UUID taskId,
                                                                 @RequestBody UpdateTaskDetailsRequest request,
                                                                 Principal principal) {

            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = principal.getName();

        TaskResponseDto updatedTask = taskService.updateTaskDetailsById(taskId, request, email);
        return ResponseEntity.ok(updatedTask);
        }
    }


