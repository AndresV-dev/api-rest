package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    private String name;
    @Column(length = 150)
    private String description;
    @Column(length = 50)
    private String genre;
    private Double price;
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean discount;
    @Column(columnDefinition = "TEXT")
    private String image;
    @Column(length = 50)
    private String category;
    private Integer stock;
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active;
}
