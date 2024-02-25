package com.andresv2.apirest.util;

import jakarta.persistence.criteria.Predicate;
import org.json.JSONObject;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.sql.Date;
import java.util.List;

@Service
public class SearchUtils<T> {

    public Specification<T> getQueryParameters(JSONObject data){

        return (root, query, cb) -> {
            Predicate[] predicates = new Predicate[data.getInt("size")];
            final int[] i = {0};

            if(data.has("equals")){
                data.getJSONObject("equals").toMap().forEach((key, value) -> {
                    if(value.getClass() == Date.class)
                        predicates[i[0]] = cb.equal(root.get(key), (Date) value);
                    else
                        predicates[i[0]] = cb.equal(root.get(key), value);
                    i[0]++;
                });
            }

            if(data.has("lessThanOrEqualTo")){// data Example
                data.getJSONObject("lessThanOrEqualTo").toMap().forEach((key, value) -> {
                    if(value.getClass() == Date.class)
                        predicates[i[0]] = cb.greaterThanOrEqualTo(root.get(key), (Date) value);
                    else
                        predicates[i[0]] = cb.lessThanOrEqualTo(root.get(key), value.toString());
                    i[0]++;
                });
            }

            if (data.has("greaterThanOrEqualTo")){// data Example { "}
                data.getJSONObject("greaterThanOrEqualTo").toMap().forEach((key, value) -> {
                    if(value.getClass() == Date.class)
                        predicates[i[0]] = cb.greaterThanOrEqualTo(root.get(key), (Date) value);
                    else
                        predicates[i[0]] = cb.greaterThanOrEqualTo(root.get(key), value.toString());
                    i[0]++; //<T, ID>
                });
            }

            if (data.has("like")){// data Example {"name": "example"}
                data.getJSONObject("like").toMap().forEach((key, value) -> {
                    predicates[i[0]] = cb.like(root.get(key), "%" + value + "%");
                    i[0]++; //<T, ID>
                });
            }

            if (data.has("between")){ // data Example {"keyBD": ["", ""]}
                JSONObject between = (JSONObject) data.get("between");
                between.toMap().forEach((key, value) -> {
                    List<String> list = Arrays.asList(value.toString().substring(1, value.toString().length() - 1).split(", "));
                    if(value.getClass() == Date.class)
                        predicates[i[0]] = cb.between(root.get(key), Date.valueOf(list.get(0)), Date.valueOf(list.get(1)));
                    else
                        predicates[i[0]] = cb.between(root.get(key), list.get(0), list.get(1));
                    i[0]++; //<T, ID>
                });
            }
            query.where(predicates);
            return query.getRestriction();
        }; //<T, ID>
    }
}
