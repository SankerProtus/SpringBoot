package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);
    Optional<Users> findByUsername(String username);
    Optional<Users> findByPhoneNumber(String phoneNumber);
    Optional<Object> findByEmail(@NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
