package com.timebank.timebank.user.dto;

import java.util.List;
import java.util.UUID;

public record UserBlockStateResponse(
        List<UUID> blockedUserIds,
        List<UUID> blockedByUserIds
) {
}
