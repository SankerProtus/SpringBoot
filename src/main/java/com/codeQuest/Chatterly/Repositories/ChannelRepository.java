package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByServerId(Long serverId);
    List<Channel> findByCategoryId(Long categoryId);
}
