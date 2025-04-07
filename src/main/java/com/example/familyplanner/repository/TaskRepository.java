package com.example.familyplanner.repository;

import com.example.familyplanner.entity.Task;
import com.example.familyplanner.entity.TaskStatus;
import com.example.familyplanner.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByAssignedTo(User user, Pageable pageable);

    Page<Task> findByFamilyId(UUID familyId, Pageable pageable);

    Page<Task> findByCreatedBy(User user, Pageable pageable);

    List<Task> findByDueDateBetweenAndCreatedBy(LocalDate startDate, LocalDate endDate, User createdBy);

    @Query("SELECT t FROM Task t WHERE " +
            "(:familyId IS NULL OR t.familyId = :familyId) AND " +
            "(:completed IS NULL OR t.completed = :completed) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:userId IS NULL OR t.assignedTo.id = :userId) AND " +
            "(:priority IS NULL OR t.priority = :priority)")
    Page<Task> findTasksWithFilters(
            @Param("familyId") UUID familyId,
            @Param("completed") Boolean completed,
            @Param("status") TaskStatus status,
            @Param("userId") UUID userId,
            @Param("priority") Integer priority,
            Pageable pageable);
}
