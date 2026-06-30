package com.sip.book_shop.common.query.utils;

import com.sip.book_shop.common.query.annotations.Query;
import com.sip.book_shop.common.utils.ObjectUtils;
import com.sip.book_shop.common.utils.ReflectionUtils;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import com.sip.book_shop.common.query.enums.Fetch;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings({"unchecked", "all"})
public class QueryHelper {

    private QueryHelper() {
    }

    /**
     * Generates a {@link Predicate} for the given query object based on the specified root,
     * criteria query, and criteria builder.
     *
     * @param <R>   The type of the root entity.
     * @param <Q>   The type of the query object.
     * @param root  The root of the query, representing the entity.
     * @param query The query object containing filter criteria.
     * @param cq    The {@link CriteriaQuery} object for the query.
     * @param cb    The {@link CriteriaBuilder} used to construct the criteria.
     * @return A {@link Predicate} representing the filter criteria.
     */
    public static <R, Q> Predicate getPredicate(Root<R> root, Q query, CriteriaQuery<?> cq, CriteriaBuilder cb) {
        if (query == null) {
            return cb.and();
        }

        List<Predicate> predicates = buildPredicates(root, query, cb, cq);

        return predicates.stream().reduce(cb::and).orElse(cb.and());
    }

    private static <R, Q> List<Predicate> buildPredicates(Root<R> root, Q query, CriteriaBuilder cb, CriteriaQuery<?> cq) {
        List<Predicate> predicates = new ArrayList<>();

        List<Field> fields = getAllFields(query.getClass());
        for (Field field : fields) {
            Predicate predicate = buildPredicateForField(root, field, query, cb, cq);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        return predicates;
    }

    private static <R, Q> Predicate buildPredicateForField(Root<R> root, Field field, Q query, CriteriaBuilder cb, CriteriaQuery<?> cq) {
        Query q = field.getAnnotation(Query.class);
        if (ObjectUtils.isEmpty(q)) {
            return null;
        }
        String attributeName = getAttributeName(q, field);
        Object val = ReflectionUtils.getValueByFieldName(query, field.getName());

        if (shouldSkipPredicate(val)) {
            return null;
        }

        Join<R, ?> join = createJoin(root, q, val);

        PredicateBuilder<R> predicateBuilder = new PredicateBuilder<>(val, field, attributeName, q.caseSensitive(), join, root, cb, cq);

        if ((q.fetchType() == Fetch.LEFT || q.fetchType() == Fetch.RIGHT) && ((cq.getResultType().equals(Long.class) || cq.getResultType().equals(long.class)))) {
            log.warn("Invalid fetch type for result type Long/long");
            return null;
        }

        if (ObjectUtils.isNotEmpty(q.blurry()) && ObjectUtils.isNotEmpty(val)) {
            return predicateBuilder.createBlurryPredicate(q.blurry());
        }

        predicateBuilder.applyFetchType(q);

        if (BooleanUtils.isTrue(isDistinct(q))) {
            cq.distinct(true);
        }
        return createPredicateForQueryType(predicateBuilder, q);
    }

    private static Boolean isDistinct(Query q) {
        return BooleanUtils.isTrue(q.distinct());
    }

    private static <R> Predicate createPredicateForQueryType(PredicateBuilder<R> predicateBuilder, Query q) {
        switch (q.type()) {
            case EQUAL:
                return predicateBuilder.createEqualPredicate();
            case NOT_EQUAL:
                return predicateBuilder.createNotEqualPredicate();
            case IS_NULL:
                return predicateBuilder.createIsNullPredicate();
            case NOT_NULL:
                return predicateBuilder.createNotNullPredicate();
            case GREATER_THAN:
                return predicateBuilder.createGreaterThanPredicate();
            case GREATER_THAN_OR_EQUAL:
                return predicateBuilder.createGreaterThanOrEqualPredicate();
            case LESS_THAN:
                return predicateBuilder.createLessThanPredicate();
            case LESS_THAN_OR_EQUAL:
                return predicateBuilder.createLessThanOrEqualPredicate();
            case INNER_LIKE:
            case REG_EXP:
                return predicateBuilder.createInnerLikePredicate();
            case LEFT_LIKE:
                return predicateBuilder.createLeftLikePredicate();
            case RIGHT_LIKE:
                return predicateBuilder.createRightLikePredicate();
            case IN_STRING:
                return predicateBuilder.createInStringPredicate();
            case IN_LONG:
                return predicateBuilder.createInLongPredicate();
            case BETWEEN:
                return predicateBuilder.createBetweenPredicate();
            default:
                throw new IllegalArgumentException("Unsupported query type: " + q.type());
        }
    }

    private static <R> Join<R, ?> createJoin(Root<R> root, Query q, Object val) {
        if (ObjectUtils.isNotEmpty(q.joinName())) {
            String[] joinNames = q.joinName().split(">");
            Join<R, ?> join = null;
            for (String name : joinNames) {
                join = createJoin(join, root, name, q.join().getJoinType(), val);
            }
            return join;
        }
        return null;
    }

    private static <R> Join<R, ?> createJoin(Join<R, ?> currentJoin, Root<R> root, String name, JoinType joinType, Object val) {
        return ObjectUtils.isNotEmpty(currentJoin) && ObjectUtils.isNotEmpty(val) ? currentJoin.join(name, joinType) : root.join(name, joinType);
    }


    private static String getAttributeName(Query q, Field field) {
        return ObjectUtils.isNotEmpty(q) && StringUtils.isBlank(q.propName()) ? field.getName() : q.propName();
    }

    private static boolean shouldSkipPredicate(Object val) {
        return val == null
                || (val instanceof String && ((String) val).isBlank())
                || (val instanceof Collection && ((Collection<?>) val).isEmpty());

    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static class PredicateBuilder<R> {
        private final Root<R> root;
        private final Field field;
        private final String attributeName;
        private String subAttribute = "";
        private final CriteriaBuilder cb;
        private final CriteriaQuery cq;
        private Join<R, ?> join;
        private boolean caseSensitive;

        private Object val;

        public PredicateBuilder(Object value, Field field, String attributeName, boolean caseSensitive, Join<R, ?> join, Root<R> root, CriteriaBuilder cb, CriteriaQuery cq) {
            this.root = root;
            this.field = field;
            this.attributeName = attributeName;
            this.cb = cb;
            this.join = join;
            this.cq = cq;
            this.val = value;
            this.caseSensitive = caseSensitive;
        }

        public <R> void applyFetchType(Query q) {
            if (q.fetchType() == Fetch.NONE) {
                return;
            }
            root.fetch(q.propName(), q.fetchType() == Fetch.LEFT ? JoinType.LEFT : JoinType.RIGHT);
            cq.distinct(true);
        }

        public Predicate createBlurryPredicate(String blurry) {
            String[] blurrys = blurry.split(",");
            List<Predicate> orPredicate = Arrays.stream(blurrys)
                    .map(s -> {
                        Expression<String> exp = getExpression(s.trim()).as(String.class);

                        if (!caseSensitive) {
                            exp = cb.lower(exp);
                        }

                        String searchVal = caseSensitive
                                ? "%" + val.toString() + "%"
                                : "%" + val.toString().toLowerCase() + "%";

                        return cb.like(exp, searchVal);
                    })
                    .collect(Collectors.toList());

            if (!orPredicate.isEmpty()) {
                Predicate[] p = new Predicate[orPredicate.size()];
                return cb.or(orPredicate.toArray(p));
            }
            return null;
        }

        public Predicate createEqualPredicate() {
            if (field.getType().isEnum()) {
                return cb.equal(getExpression(attributeName), val);
            }

            if (field.getType().equals(String.class)) {
                Expression<String> exp = getExpression(attributeName).as(String.class);

                if (!caseSensitive) {
                    return cb.equal(cb.lower(exp), val.toString().toLowerCase());
                }

                return cb.equal(exp, val.toString());
            }
            return cb.equal(getExpression(attributeName).as(field.getType()), val);
        }

        public Predicate createNotEqualPredicate() {
            return cb.notEqual(getExpression(attributeName), val);
        }

        public Predicate createIsNullPredicate() {
            return cb.isNull(getExpression(attributeName));
        }

        public Predicate createNotNullPredicate() {
            return cb.isNotNull(getExpression(attributeName));
        }

        public Predicate createGreaterThanPredicate() {
            return cb.greaterThan(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createGreaterThanOrEqualPredicate() {
            return cb.greaterThanOrEqualTo(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createLessThanPredicate() {
            return cb.lessThan(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createLessThanOrEqualPredicate() {
            return cb.lessThanOrEqualTo(getExpression(attributeName).as((Class<? extends Comparable>) field.getType()), (Comparable) val);
        }

        public Predicate createInnerLikePredicate() {
            return buildLikePredicate("%" + val + "%", caseSensitive);
        }

        public Predicate createLeftLikePredicate() {
            return buildLikePredicate("%" + val, caseSensitive);
        }

        public Predicate createRightLikePredicate() {
            return buildLikePredicate(val + "%", caseSensitive);
        }

        public Predicate createRegularExpressionPredicate() {
            return cb.like(getExpression(attributeName).as(String.class), "%" + val + "%");
        }

        public Predicate createInStringPredicate() {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getExpression(attributeName).in((List<String>) val);
        }

        public Predicate createInLongPredicate() {
            if (ObjectUtils.isEmpty(val)) {
                return null;
            }
            return getExpression(attributeName).in((Collection<String>) val);
        }

        public Predicate createBetweenPredicate() {
            List<Object> betweenObj = new ArrayList<>((List<Object>) val);
            List<Timestamp> between = new ArrayList<>();

            // Ensure we have exactly two values
            if (betweenObj != null && betweenObj.size() == 2) {
                ZoneId zoneId = ZoneId.systemDefault();

                // Start Date at 00:00:00
                if (betweenObj.get(0) instanceof java.util.Date startDate) {
                    LocalDateTime startOfDay = startDate.toInstant()
                            .atZone(zoneId)
                            .toLocalDate()
                            .atStartOfDay();
                    between.add(Timestamp.valueOf(startOfDay));
                }

                // End Date at 23:59:59
                if (betweenObj.get(1) instanceof java.util.Date endDate) {
                    LocalDateTime endOfDay = endDate.toInstant()
                            .atZone(zoneId)
                            .toLocalDate()
                            .atTime(23, 59, 59);
                    between.add(Timestamp.valueOf(endOfDay));
                }
            }
            return (between.size() == 2) ? createBetweenPredicate(between) : null;
        }

        private Predicate createBetweenPredicate(List<Timestamp> between) {
            if (between.isEmpty()) {
                return null;
            }

            Expression<? extends Comparable> expression = getExpression(attributeName).as((Class<? extends Comparable>) between.get(0).getClass());

            return cb.between(expression, (Comparable) between.get(0), (Comparable) between.get(1));
        }

        private Predicate buildLikePredicate(String pattern, boolean caseSensitive) {
            Expression<String> exp = getExpression(attributeName).as(String.class);

            if (!caseSensitive) {
                return cb.like(cb.lower(exp), pattern.toLowerCase());
            }

            return cb.like(exp, pattern);
        }


        private Expression<?> getExpression(String attributeName) {
            Path path = ObjectUtils.isNotEmpty(join) ? join : root;
            String[] attributeNames = attributeName.split("\\.");

            for (String attributeNamePart : attributeNames) {
                try {
                    path = path.get(attributeNamePart);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid attribute name or path: " + attributeName, e);
                }
            }
            return path;
        }
    }
}
