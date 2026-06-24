package com.sip.book_shop.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ObjectUtils {
    private static final ObjectMapper objectMapper = configureObjectMapper();
    private static final String REFLECTION_ERROR_MESSAGE = "Reflection error while checking object emptiness";

    private ObjectUtils() {
    }

    public static boolean isEmpty(Object obj) {
        return org.apache.commons.lang3.ObjectUtils.isEmpty(obj);
    }

    public static boolean isNotEmpty(Object obj) {
        return org.apache.commons.lang3.ObjectUtils.isNotEmpty(obj);
    }

    /**
     * Returns the provided object if it is not null; otherwise, returns the default value.
     *
     * @param <T>          The type of the object and default value.
     * @param object       The object to check for null.
     * @param defaultValue The default value to return if the object is null.
     * @return The object itself if not null; otherwise, the default value.
     */
    public static <T> T defaultIfNull(T object, T defaultValue) {
        return org.apache.commons.lang3.ObjectUtils.isNotEmpty(object) ? object : defaultValue;
    }

    /**
     * Configures and returns an {@link ObjectMapper} instance with specific settings.
     * <p>
     * The returned {@link ObjectMapper} has case-insensitive property acceptance enabled
     * and does not fail on unknown properties during deserialization.
     * </p>
     *
     * @return The configured {@link ObjectMapper} instance.
     */
    public static ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .build();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * Converts the given object to its JSON string representation using the configured {@link ObjectMapper}.
     *
     * @param <T>    The type of the object to be converted to JSON.
     * @param object The object to be converted to JSON.
     * @return The JSON string representation of the object, or {@code null} if an error occurs during conversion.
     */
    public static <T> String serializeToJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error serializing object to JSON", e);
        }
    }

    public static <T> T deserializeFromJson(String json, Class<T> clazz) {
        try {
            return ObjectUtils.configureObjectMapper().readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error deserializing JSON to object", e);
        }
    }

    public static <T> T requireNonEmpty(T obj) {
        return org.apache.commons.lang3.ObjectUtils.requireNonEmpty(obj);
    }

    public static Map<String, Object> convertJsonToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            map.put(key, value);
        }
        return map;
    }

    public static <T> List<T> removeAllEmpty(List<T> list) {
        return list.stream()
                .filter(ObjectUtils::hasAnyValue)
                .toList();
    }

    public static boolean hasAnyValue(Object obj) {
        if (obj == null) {
            return false;
        }

        try {
            for (Field field : ReflectionUtils.getFields(obj.getClass())) {
                field.trySetAccessible();
                Object value = field.get(obj);

                if (isNotEmptyValue(value)) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(REFLECTION_ERROR_MESSAGE, e);
        }

        return false;
    }

    public static <T> List<T> isAllEmpty(List<T> list) {
        return list.stream()
                .filter(obj -> !hasAnyValue(obj))
                .toList();
    }

    private static boolean isNotEmptyValue(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof String s) {
            return !s.trim().isEmpty();
        }

        if (value instanceof Number n) {
            return n.doubleValue() != 0.0;
        }

        return true;
    }
}
