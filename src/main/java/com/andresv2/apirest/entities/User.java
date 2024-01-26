package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String name;
    private String lastname;
    private Integer age;
    @NotEmpty(message = " cannot be null or empty")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "password")
    @NotEmpty(message = " cannot be null or empty")
    private String password;
    private String email;
    private String json;
    @Transient
    private String token;
    @Transient
    private String error;
    @Transient
    private List<Rol> roles;

    public User(String error){
    	this.error = error;
    }

}
