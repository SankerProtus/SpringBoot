package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Servers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Servers, Long> {
    List<Servers> findByMembersUserId(Long userId);
    boolean existsByIdAndMembersUserId(Long serverId, Long userId);
}