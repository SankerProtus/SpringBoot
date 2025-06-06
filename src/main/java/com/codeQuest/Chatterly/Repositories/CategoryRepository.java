package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByServerId(Long serverId);
}
