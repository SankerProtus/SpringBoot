package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.ServerMember;
import com.codeQuest.Chatterly.Entities.Servers;
import com.codeQuest.Chatterly.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {
    boolean existsByServerAndUser(Servers server, Users user);
    Optional<ServerMember> findByServerIdAndUserId(Long serverId, Long userId);
}