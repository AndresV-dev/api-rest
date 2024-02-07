package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    private String uuid;
    private String title;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_City")
    private Date created_at;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_City")
    private Date end_at;
    private String collection;
    private String category;
    @OneToOne
    private TaskPriority priority;
    private Long user_id;
}
