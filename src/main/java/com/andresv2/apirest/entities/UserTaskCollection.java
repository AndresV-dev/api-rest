package com.andresv2.apirest.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_task_collections")
public class UserTaskCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    @Column(length = 100)
    private String description;
    @Column(name = "user_id")
    private Long userId;
    @OneToMany(mappedBy = "collection_id", targetEntity = CollectionCategory.class)
    private List<CollectionCategory> categories;
    @Transient
    private String error;

    public UserTaskCollection(String error) {
        this.error = error;
    }
}
