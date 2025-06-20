package com.codeQuest.Chatterly.Entities;

import com.codeQuest.Chatterly.Enums.ChannelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;


    @Enumerated(EnumType.STRING)
    private ChannelType type;

    private int position;

    @ManyToOne
    private Category category;

    @ManyToOne
    private Servers server;

    @ManyToMany
    @JoinTable(
        name = "channel_users",
        joinColumns = @JoinColumn(name = "channel_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Message> messages;
}