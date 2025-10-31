package com.bugtracker.projectservice.model;

import com.bugtracker.projectservice.enums.ProjectType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type")
    private ProjectType projectType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "created_by_username")
    private String createdByUsername;

    @Column(name = "member_user_ids", columnDefinition = "TEXT")
    private String memberUserIds;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Set<Long> getMemberIds() {
        if (memberUserIds == null || memberUserIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> ids = new HashSet<>();
        for (String id : memberUserIds.split(",")) {
            try {
                ids.add(Long.parseLong(id.trim()));
            } catch (NumberFormatException e) {
            }
        }
        return ids;
    }

    public void setMemberIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            this.memberUserIds = null;
        } else {
            this.memberUserIds = String.join(",", ids.stream().map(String::valueOf).toArray(String[]::new));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return id != null && id.equals(project.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
