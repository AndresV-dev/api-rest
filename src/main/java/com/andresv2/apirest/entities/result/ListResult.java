package com.andresv2.apirest.entities.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper=true)
public class ListResult<T> extends Result<T> {

    private Optional<List<T>> values;
    private Integer registers;
    private Integer code;

    public static <T> ListResult<T> failed(String errorMessage, Integer code) {
        ListResult<T> result = new ListResult<T>();
        result.setStatus(ResultStatus.FAILED);
        result.setErrorMessage(errorMessage);
        result.setCode(code != null ? code : 500);
        return result;
    }

    public static <T> ListResult<T> ok(List<T> values, Integer registers) {
        ListResult<T> result = new ListResult<T>();
        result.setStatus(ResultStatus.OK);
        result.setValues(Optional.of(values));
        result.setRegisters(registers);
        return result;
    }
}
