package com.andresv2.apirest.exception;

import com.andresv2.apirest.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;
import org.json.JSONObject;
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
import java.util.HashMap;
import java.util.List;

@RequestMapping
@RestControllerAdvice
public class CustomExceptionHandler {

    String[] errorPart;
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request){
        ErrorResponse error = new ErrorResponse(400,"Bad Credentials", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        List<String> errors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            errors.add(((FieldError) error).getField() + error.getDefaultMessage());
        });
        ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(),ex.getBody().getDetail(), errors, request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MysqlDataTruncation.class)
    public ResponseEntity<Object> handleMysqlDataTruncation(MysqlDataTruncation ex, WebRequest request){
        errorPart =  ex.getMessage().split(":");
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(),errorPart[0], errorPart[1], request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex, WebRequest request){
        errorPart = ex.getMessage().split(":");
        ErrorResponse error = new ErrorResponse(500 ,errorPart[0], errorPart[1], request.getDescription(false));
        return new ResponseEntity<>(error , HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
