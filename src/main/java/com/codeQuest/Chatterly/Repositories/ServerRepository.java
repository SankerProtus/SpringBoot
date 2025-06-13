package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Servers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerRepository extends JpaRepository<Servers, Long> {
}