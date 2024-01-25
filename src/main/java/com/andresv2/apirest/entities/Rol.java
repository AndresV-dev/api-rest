package com.andresv2.apirest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rol {
    private String rol;
    private String name;
    private String canal;
    private Integer level;
    private String note;
}
