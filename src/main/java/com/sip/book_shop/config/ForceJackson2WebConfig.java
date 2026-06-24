package com.sip.book_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sip.book_shop.common.utils.ObjectUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class ForceJackson2WebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
        // 1. Grab your legacy Jackson 2 ObjectMapper from ObjectUtils
        final ObjectMapper legacyMapper = ObjectUtils.configureObjectMapper();

        // 2. Create a clean, non-deprecated HTTP message converter wrapper
        AbstractHttpMessageConverter<Object> legacyConverter = new AbstractHttpMessageConverter<>(MediaType.APPLICATION_JSON) {
            @Override
            protected boolean supports(Class<?> clazz) {
                return true; // Handle all web payload objects passing through this converter
            }

            @Override
            protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
                    throws IOException, HttpMessageNotReadableException {
                // Use Jackson 2 to read the network stream into the target class types
                return legacyMapper.readValue(inputMessage.getBody(), clazz);
            }

            @Override
            protected void writeInternal(Object o, HttpOutputMessage outputMessage)
                    throws IOException, HttpMessageNotWritableException {
                // Use Jackson 2 to serialize outbound data
                legacyMapper.writeValue(outputMessage.getBody(), o);
            }
        };

        // 3. Register it at the highest priority (front of the line)
        builder.addCustomConverter(legacyConverter);
    }
}
