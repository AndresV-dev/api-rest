package com.andresv2.apirest.entities.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private ResultStatus status;
    private Optional<T> value;
    private String errorMessage;
    private Integer code;

    public static <T> Result<T> failed(String errorMessage) {
        Result<T> result = new Result<>();
        result.setStatus(ResultStatus.FAILED);
        result.setCode(500);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static <T> Result<T> failed(String errorMessage, Integer code) {
        Result<T> result = new Result<>();
        result.setStatus(ResultStatus.FAILED);
        result.setCode(code);
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static <T> Result<T> ok(T value) {
        Result<T> result = new Result<>();
        result.setStatus(ResultStatus.OK);
        result.setCode(200);
        result.setValue(Optional.ofNullable(value));
        return result;
    }

    public boolean isValid() { return this.status.equals(ResultStatus.OK) && this.value != null && this.value.isPresent();}

    public boolean isSuccessful() {
        return this.getStatus().equals(ResultStatus.OK);
    }
}
