package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
//    @Query("SELECT s FROM User_Table s WHERE s.email = ?1")
    Optional<Users> findByUsername(String username);
    boolean existsByEmail(String email);
}
