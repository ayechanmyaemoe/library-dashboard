package com.sip.book_shop.common.query.enums;

import jakarta.persistence.criteria.JoinType;
import lombok.Getter;

@Getter
public enum Join {
    LEFT(JoinType.LEFT),
    RIGHT(JoinType.RIGHT),
    INNER(JoinType.INNER);

    private final JoinType joinType;

    Join(JoinType joinType) {
        this.joinType = joinType;
    }
}
