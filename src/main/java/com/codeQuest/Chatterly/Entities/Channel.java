package com.codeQuest.Chatterly.Entities;

import com.codeQuest.Chatterly.Enums.ChannelType;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToMany(mappedBy = "channels")
    private Set<Users> users = new HashSet<>();

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<Messages> messages;

}
