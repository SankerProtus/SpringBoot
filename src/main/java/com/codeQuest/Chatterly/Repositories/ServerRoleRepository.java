package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.ServerRole;
import com.codeQuest.Chatterly.Entities.Servers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRoleRepository extends JpaRepository<ServerRole, Long> {
    List<ServerRole> findByServerId(Long serverId);
    Optional<ServerRole> findByServerIdAndId(Long serverId, Long id);
    Optional<ServerRole> findByServerAndName(Servers server, String name);
}