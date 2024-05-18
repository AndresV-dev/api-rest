package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

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
    @NotEmpty(message = " cannot be null or empty")
    private String title;
    @Column(length = 150)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") // ALSO CAN BE America/Mexico_City for mexico city -8 hours
    @CreatedDate()
    @Column(name = "created_at", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    @Column(name = "end_at", columnDefinition = "datetime default CURRENT_TIMESTAMP")
    private LocalDateTime endAt;
    @Column(name = "collection_id")
    private Integer collectionId;
    @Column(name = "category_id")
    private Integer categoryId;
    @Column(name = "priority_id")
    private Integer priorityId;
    @Column(name = "user_id")
    private Long userId;

    @Transient
    private String collection;
    @Transient
    private String category;
    @Transient
    private Integer registers;
}
