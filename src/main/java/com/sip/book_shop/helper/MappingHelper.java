package com.sip.book_shop.helper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MappingHelper {

    @Named("trimString")
    public String trimString(String value) {
        return value != null ? value.trim() : null;
    }
}
