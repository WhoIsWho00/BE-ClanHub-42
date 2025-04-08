package com.example.familyplanner.service.converter;

import com.example.familyplanner.dto.responses.task.TaskResponseDto;
import com.example.familyplanner.dto.responses.task.TaskResponseInCalendarDto;
import com.example.familyplanner.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskConverter {

    private final UserConverter userConverter;

    public TaskResponseDto convertToDto(Task task) {
        if (task == null) {
            return null;
        }

        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setCompleted(task.isCompleted());
        dto.setStatus(task.getStatus());
        dto.setFamilyId(task.getFamilyId());
        dto.setPriority(task.getPriority());
        dto.setCompletionDate(task.getCompletionDate());

        if (task.getAssignedTo() != null) {
            dto.setAssignedTo(userConverter.createDtoFromUser(task.getAssignedTo()));
        }
        if (task.getCreatedBy() != null) {
            dto.setCreatedBy(userConverter.createDtoFromUser(task.getCreatedBy()));
        }

        return dto;
    }

    public List<TaskResponseInCalendarDto> convertTasksForCalendarToDto(List<Task> taskList) {

        if(taskList == null ) {
            throw new NullPointerException("taskList is null");
        }

        List<TaskResponseInCalendarDto> dtoList = new ArrayList<>();

        for (Task task : taskList) {
            TaskResponseInCalendarDto dto = new TaskResponseInCalendarDto();

            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setDescription(task.getDescription());
            dto.setDueDate(task.getDueDate());
            dto.setCreatedAt(task.getCreatedAt());
            dto.setCompleted(task.isCompleted());
//            dto.setFamilyId(task.getFamilyId());
            dto.setPriority(task.getPriority());
            dto.setCompletionDate(task.getCompletionDate());


            if (task.getAssignedTo() != null) {
                dto.setAssignedTo(userConverter.createDtoFromUser(task.getAssignedTo()));
            }
            if (task.getCreatedBy() != null) {
                dto.setCreatedBy(userConverter.createDtoFromUser(task.getCreatedBy()));
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<TaskResponseDto> convertTasksToDto(List<Task> taskList) {

        if(taskList == null ) {
            throw new NullPointerException("taskList is null");
        }

        List<TaskResponseDto> dtoList = new ArrayList<>();

        for (Task task : taskList) {
            TaskResponseDto dto = new TaskResponseDto();

            dto.setId(task.getId());
            dto.setTitle(task.getTitle());
            dto.setDescription(task.getDescription());
            dto.setDueDate(task.getDueDate());
            dto.setCreatedAt(task.getCreatedAt());
            dto.setCompleted(task.isCompleted());
//          dto.setFamilyId(task.getFamilyId());
            dto.setPriority(task.getPriority());
            dto.setCompletionDate(task.getCompletionDate());


            if (task.getAssignedTo() != null) {
                dto.setAssignedTo(userConverter.createDtoFromUser(task.getAssignedTo()));
            }
            if (task.getCreatedBy() != null) {
                dto.setCreatedBy(userConverter.createDtoFromUser(task.getCreatedBy()));
            }

            dtoList.add(dto);
        }

        return dtoList;
    }
}