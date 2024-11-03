package com.andresv2.apirest.dto;

import org.springframework.stereotype.Component;

@Component
public class ResponseDto {
    private Integer status;
    private Boolean success;
    private String message;
    private Object data;

    private ResponseDto() {

    }

    public ResponseDto success(String message, Object data){
        this.status = 200;
        this.success = true;
        this.message = message;
        this.data = data;
        return this;
    }

    public ResponseDto error(String message, Object data){
        this.status = 500;
        this.success = false;
        this.message = "An Error has Ocurred: [" + message + "]";
        this.data = data;
        return this;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}