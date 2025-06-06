package com.codeQuest.Chatterly.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(name = "idx_server_member_server", columnList = "server_id"),
        @Index(name = "idx_server_member_user_server", columnList = "server_id,user_id")
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

public class ServerMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Servers server;

    @ManyToMany
    private Set<ServerRole> roles;

    @Column(nullable = false)
    private LocalDateTime joinedAt;
}
