package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @UuidGenerator
    @Column(length = 36, unique = true)
    private String uuid;
    @Column(name = "status", length = 50)
    private String status;
    @Column(length = 50)
    private String title;
    @Column(length = 150)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_City")
    @Column(name = "created_at")
    private Date createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_City")
    @Column(name = "end_at")
    private Date endAt;
    @Column(name = "collection_id")
    private Integer collectionId;
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name = "priority_id")
    private Integer priorityId;
    @Column(name = "user_id")
    private Long userId;
}
