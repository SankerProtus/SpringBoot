package com.codeQuest.Chatterly.Repositories;

import com.codeQuest.Chatterly.Entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(@NotBlank(message = "Email is required")
                                @Email(message = "Email should be valid")
                                String email);

    Optional<User> findByUsername(String username);
}
