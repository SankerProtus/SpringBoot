package com.codeQuest.Chatterly.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "servers")
@Data
public class Servers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String serverIcon;
    private LocalDateTime createdAt;

    @ManyToOne
    private User owner;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<Category> categories;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<Channel> channels;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<ServerRole> roles;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<ServerMember> members;

    @ManyToOne
    private User serverOwner;

}
