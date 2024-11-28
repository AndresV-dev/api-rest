package com.andresv2.apirest.util;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

@UtilityClass
public class RecursiveMethodsUtils {

    public Pageable getPageable(Map<String, Object> filters, Integer page, Integer size, String sortBy) {
        int pageNumber = page != null ? page : filters.containsKey("page") ? (Integer) filters.get("page") : 0;
        int pageSize = size != null ? size : filters.containsKey("size") ? (Integer) filters.get("size") : 5;
        String sort = sortBy != null ? sortBy : filters.containsKey("groupBy") ? (String) filters.get("groupBy") : "id";

        return PageRequest.of(pageNumber, pageSize, Sort.by(sort).descending());
    }
}
