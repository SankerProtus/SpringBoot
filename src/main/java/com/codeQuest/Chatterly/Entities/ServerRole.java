package com.codeQuest.Chatterly.Entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

@Entity
@Table(name = "server_role", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "server_id"}),
    indexes = {
        @Index(name = "idx_server_role_name", columnList = "name")
    }
)
@Getter
@Setter
public class ServerRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    @Size(max = 50)
    @Nullable
    private String color;

    @ElementCollection
    @CollectionTable(
            name = "server_role_permissions",
            joinColumns = @JoinColumn(name = "role_id")
    )
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "server_id")
    @NotNull
    private Servers server;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerRole)) return false;
        ServerRole that = (ServerRole) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}