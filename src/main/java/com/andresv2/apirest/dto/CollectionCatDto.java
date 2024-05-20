package com.andresv2.apirest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CollectionCatDto {
    @Id
    private Long id;
    private String collection;
    private String category;
    private Long registers;

    public CollectionCatDto(Long id, String collection, Long registers){
        this.id = id;
        this.collection = collection;
        this.registers = registers;
    }
}

