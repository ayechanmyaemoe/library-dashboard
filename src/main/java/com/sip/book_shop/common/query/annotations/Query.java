package com.sip.book_shop.common.query.annotations;

import com.sip.book_shop.common.query.enums.Fetch;
import com.sip.book_shop.common.query.enums.Join;
import com.sip.book_shop.common.query.enums.Type;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    String propName() default "";

    Type type() default Type.EQUAL;


    /**
     * The attribute name of the connection query, such as userRole in the User class
     */
    String joinName() default "";

    /**
     * Default left connection
     */
    Join join() default Join.LEFT;

    /**
     * The attribute is to fetch lazy object
     */
    Fetch fetchType() default Fetch.NONE;

    /**
     * Multi-field fuzzy search, only supports String type fields, multiple separated by commas, such as @Query (blurry = "email, username")
     */
    String blurry() default "";

    boolean distinct() default false;

    boolean caseSensitive() default false;
}
