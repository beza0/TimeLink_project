package com.timebank.timebank.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserBlockRepository extends JpaRepository<UserBlock, UUID> {

    boolean existsByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);

    void deleteByBlocker_IdAndBlocked_Id(UUID blockerId, UUID blockedId);

    @Query("select ub.blocked.id from UserBlock ub where ub.blocker.id = :blockerId")
    List<UUID> findBlockedUserIds(UUID blockerId);

    @Query("select ub.blocker.id from UserBlock ub where ub.blocked.id = :blockedId")
    List<UUID> findBlockedByUserIds(UUID blockedId);
}
