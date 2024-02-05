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
    private String name;
    private String description;
    private String genre;
    private Double price;
    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean discount;
    private String image;
    private String category;
    private Integer stock;
    @Column(columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean active;
}
