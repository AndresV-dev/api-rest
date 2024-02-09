package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "store")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 50)
    @NotEmpty(message = " cannot be null or empty")
    private String name;
    @NotEmpty(message = " cannot be null or empty")
    @Column(length = 150)
    private String description;
    @NotEmpty(message = " cannot be null or empty")
    @Column(length = 50)
    private String ramo; // technology, financial
    @Transient
    private List<Category> categories;
    @Transient
    private String error;

    public Store(String error) {
        this.error = error;
    }
}
