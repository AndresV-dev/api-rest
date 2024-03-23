package com.andresv2.apirest.exception;

import com.andresv2.apirest.dto.ErrorResponseDto;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class CustomExceptionHandler {

    String[] errorPart;
    Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request){
        ErrorResponseDto error = new ErrorResponseDto(400,"Bad Credentials", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(((FieldError) error).getField() + error.getDefaultMessage());
        });
        ErrorResponseDto error = new ErrorResponseDto(ex.getStatusCode().value(),ex.getBody().getDetail(), errors, request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MysqlDataTruncation.class)
    public ResponseEntity<Object> handleTokenExpiredException(TokenExpiredException ex, WebRequest request){
        errorPart =  ex.getMessage().split(":");
        ErrorResponseDto error = new ErrorResponseDto(ex.hashCode(), errorPart[0], errorPart[1].trim(), request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, WebRequest request){
        ErrorResponseDto error = new ErrorResponseDto(500 ,HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
