package com.timebank.timebank.skill.dto;

import java.time.Instant;
import java.util.UUID;

public class SkillResponse {

    private UUID id;
    private String title;
    private String description;
    private int durationMinutes;
    private UUID ownerId;
    private String ownerName;
    private Instant createdAt;

    public SkillResponse(UUID id,
                         String title,
                         String description,
                         int durationMinutes,
                         UUID ownerId,
                         String ownerName,
                         Instant createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}