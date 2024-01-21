package com.andresv2.apirest.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CredentialsDto {

    private String username;
    private String email;
    private char[] password;
}
