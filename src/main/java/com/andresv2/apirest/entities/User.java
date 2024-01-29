package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_city")
    private Date created_at;
    private String role;
    private String name;
    private String lastname;
    private Integer age;
    @NotEmpty(message = " cannot be null or empty")
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "password") // only is accessible on the creation of user, on jsonresponse is not shown
    @NotEmpty(message = " cannot be null or empty")
    private String password;
    private String email;
    private String json;
/* Actually is not required, because the user only can have one role, and it is saved on String role key
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role", referencedColumnName = "role")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // avoid circular dependency, avoid infinite loop, avoid infinite recursion, avoid infinite loop, avoid infinite recursion, avoid infinite loop, avoid infinite recursion, avoid infinite loop, avoid infinite recursion, avoid infinite loop, avoid infinite recursion, avoid infinite loop, avoid infinite recursion, avoid infinite loo
    private Role role;
*/
    @Transient
    private String token;
    @Transient
    private String error;

    public User(String error){
    	this.error = error;
    }

}
