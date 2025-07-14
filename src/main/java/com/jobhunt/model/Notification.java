package com.jobhunt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "related_entity_id")
    private Long relatedEntityId;  // ID of job, application, etc.

    // Notification type enum
    public enum NotificationType {
        JOB_APPLICATION_UPDATE,
        NEW_JOB_MATCH,
        MESSAGE_RECEIVED,
        PROFILE_VIEW,
        SYSTEM
    }
}
