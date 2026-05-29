package com.sip.book_shop.entities.queryCriteria;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sip.book_shop.common.query.annotations.Query;
import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.common.vo.NzDataTableInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryCriteria {

    @Query(blurry = "username, email")
    private String blurry;

    private Pageable pageable;

    public static UserQueryCriteria deserializedJsonToCriteria(JsonNode json) {
        ObjectMapper mapper = ObjectUtils.configureObjectMapper();
        try {
            return mapper.treeToValue(json, UserQueryCriteria.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserQueryCriteria createUserQueryCriteria(NzDataTableInput dataTableInput) {
        UserQueryCriteria criteria = deserializedJsonToCriteria(dataTableInput.getQueryCriteria());
        criteria.setPageable(dataTableInput.getPageable());
        return criteria;
    }
}
