package com.sip.book_shop.entities.queryCriteria;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sip.book_shop.common.query.annotations.Query;
import com.sip.book_shop.common.query.enums.Type;
import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.common.vo.NzDataTableInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookQueryCriteria {

    @Query(blurry = "title")
    private String blurry;

    @Query(type = Type.EQUAL, joinName = "author", propName = "name")
    private String author;

    @Query(type = Type.IN_STRING, joinName = "author", propName = "name")
    private List<String> authors;

    @Query(type = Type.EQUAL, joinName = "category", propName = "name")
    private String category;

    @Query(type = Type.GREATER_THAN_OR_EQUAL, propName = "publishedYear")
    private Long publishedFrom;

    @Query(type = Type.LESS_THAN_OR_EQUAL, propName = "publishedYear")
    private Long publishedUntil;

    private Pageable pageable;

    public static BookQueryCriteria deserializedJsonToCriteria(JsonNode json) {
        ObjectMapper mapper = ObjectUtils.configureObjectMapper();
        try {
            return mapper.treeToValue(json, BookQueryCriteria.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static BookQueryCriteria createBookQueryCriteria(NzDataTableInput dataTableInput) {
        BookQueryCriteria criteria = deserializedJsonToCriteria(dataTableInput.getQueryCriteria());
        criteria.setPageable(dataTableInput.getPageable());
        return criteria;
    }
}
