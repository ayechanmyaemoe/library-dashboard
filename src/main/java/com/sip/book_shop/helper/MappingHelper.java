package com.sip.book_shop.helper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class MappingHelper {

    @Named("trimString")
    public String trimString(String value) {
        return value != null ? value.trim() : null;
    }

    @Named("trimAndUpperCase")
    public String trimAndUpperCase(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }
}
