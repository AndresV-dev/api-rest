package com.andresv2.apirest.entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

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
    @UuidGenerator
    @Column(length = 36, unique = true, nullable = false)
    private String uuid;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "America/Mexico_city")
    @Column(length = 20, columnDefinition = "Datetime default current_timestamp")
    private Date created_at;
    @Column(length = 50)
    private String role;
    @Column(length = 50)
    private String name;
    @Column(length = 100)
    private String lastname;
    private Integer age;
    @NotEmpty(message = " cannot be null or empty")
    @Column(length = 50)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value = "password") // only is accessible on the creation of user, on json Response is not shown
    @NotEmpty(message = " cannot be null or empty")
    @Column(length = 80)
    private String password;
    @Column(length = 80)
    private String email;
    @Column(columnDefinition = "TEXT")
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
