package com.codeQuest.Chatterly.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer position;

    @ManyToOne
    private Servers server;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Channel> channels;

}
