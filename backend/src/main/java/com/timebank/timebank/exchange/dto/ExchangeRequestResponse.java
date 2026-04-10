package com.timebank.timebank.exchange.dto;

import com.timebank.timebank.exchange.ExchangeRequestStatus;

import java.time.Instant;
import java.util.UUID;

public class ExchangeRequestResponse {

    private UUID id;
    private UUID skillId;
    private String skillTitle;
    private UUID requesterId;
    private String requesterName;
    private UUID ownerId;
    private String ownerName;
    private String message;
    private ExchangeRequestStatus status;
    private Instant createdAt;

    public ExchangeRequestResponse(
            UUID id,
            UUID skillId,
            String skillTitle,
            UUID requesterId,
            String requesterName,
            UUID ownerId,
            String ownerName,
            String message,
            ExchangeRequestStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.skillId = skillId;
        this.skillTitle = skillTitle;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public String getSkillTitle() {
        return skillTitle;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getMessage() {
        return message;
    }

    public ExchangeRequestStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}