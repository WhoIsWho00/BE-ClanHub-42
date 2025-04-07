package com.example.familyplanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 300)
    private String description;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "completed")
    private boolean completed;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "family_id")
    private UUID familyId;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "completion_date")
    private LocalDate completionDate;

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDate.now();
        if (status == null) {
            status = TaskStatus.NEW;
    }
}

public void setCompleted(boolean completed) {
    this.completed = completed;
    if (completed) {
        this.status = TaskStatus.COMPLETED;
    } else if (this.status == TaskStatus.COMPLETED) {
        // Якщо статус був COMPLETED, а completed стає false, встановлюємо IN_PROGRESS
        this.status = TaskStatus.IN_PROGRESS;
    }
}
    public void setStatus(TaskStatus status) {
        this.status = status;
        this.completed = (status == TaskStatus.COMPLETED);

        if (status == TaskStatus.COMPLETED) {
            this.completionDate = LocalDate.now();
        }
    }
}

