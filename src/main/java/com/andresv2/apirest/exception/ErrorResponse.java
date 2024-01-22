package com.andresv2.apirest.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private Integer code;
    private String type;
    private Object message;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp = new Date();
    private String path;


    public ErrorResponse(Integer code, String type, Object message, String path) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.path = path.replace("uri=", "");
    }
}
