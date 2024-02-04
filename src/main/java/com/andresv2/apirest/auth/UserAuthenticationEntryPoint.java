package com.andresv2.apirest.auth;

import com.andresv2.apirest.dto.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    Logger logger =  LoggerFactory.getLogger(UserAuthenticationEntryPoint.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        if (request.getAttribute("Exception") != null) {
            String[] error = request.getAttribute("Exception").toString().split(":");
            OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), error[0], error[1].trim(), request.getAttribute("Exception_source").toString()));
        }else {
            OBJECT_MAPPER.writeValue(response.getOutputStream(), new ErrorResponseDto(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.name(), authException.getMessage(), null));
        }
    }
}
