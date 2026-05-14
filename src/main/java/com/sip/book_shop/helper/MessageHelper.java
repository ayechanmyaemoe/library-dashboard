package com.sip.book_shop.helper;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MessageHelper {

    private static MessageSource messageSource;

    public MessageHelper(MessageSource messageSource) {
        MessageHelper.messageSource = messageSource;
    }

    public static String getMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key;
        }
    }
}
