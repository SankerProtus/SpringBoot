package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.ServerMember;
import com.codeQuest.Chatterly.Entities.Servers;
import com.codeQuest.Chatterly.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {
    boolean existsByServerAndUser(Servers server, User user);
    Optional<ServerMember> findByServerIdAndUserId(Long serverId, Long userId);
//    boolean existsByServerIdAndUserId(Long serverId, Long  userId);
}