package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.model.MessageFilter;
import com.consol.citrus.simulator.model.MessageHeader;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRepository {

    /**
     * HeaderFilter should be of format: p1:v1;p2:v2;p3:v3 with p_x being the
     * parameter name and v_x being the parameter value to filter. This pattern is
     * used to divide the filter string into respective tuples.
     */
    private static final Pattern HEADER_FILTER_STRING_PATTERN = Pattern.compile("([^:;]+):([^:;]+);?");

    protected void addDirectionPredicate(MessageFilter filter, CriteriaBuilder criteriaBuilder, Path<Message> path,
            List<Predicate> predicates) {

        if (!filter.getDirectionInbound()) {
            predicates.add(criteriaBuilder.notEqual(path.get("direction"), Direction.INBOUND));
        }

        if (!filter.getDirectionOutbound()) {
            predicates.add(criteriaBuilder.notEqual(path.get("direction"), Direction.OUTBOUND));
        }
    }

    protected <T> void joinHeader(CriteriaBuilder criteriaBuilder, String headerFilter, From<T, Message> from,
            Function<From<T, Message>, Join<Message, MessageHeader>> joinFunction) {

        if (headerFilter == null) {
            return;
        }

        String escapedFilter = escapedToSafeString(headerFilter);
        Matcher matcher = HEADER_FILTER_STRING_PATTERN.matcher(escapedFilter);

        List<Predicate> headerPredicates = new ArrayList<>();
        Join<Message, MessageHeader> join = null;
        while (matcher.find()) {

            String headerParam = safeStringToUnescapedCharacters(matcher.group(1));
            String headerValue = safeStringToUnescapedCharacters(matcher.group(2));

            if (join == null) {
                join = joinFunction.apply(from);
            }

            headerPredicates.add(criteriaBuilder.and(criteriaBuilder.equal(join.get("name"), headerParam),
                    criteriaBuilder.like(join.get("value"), headerValue)));
        }

        if (!headerPredicates.isEmpty()) {
            join.on(criteriaBuilder.or(headerPredicates.toArray(new Predicate[0])));
        }
    }

    /**
     * Replace escaped chars by dedicated unique strings
     * 
     * @param escapedString
     * @return
     */
    private String escapedToSafeString(String escapedString) {
        return escapedString != null ? escapedString.replace("\\:", "%COLON%").replace("\\;", "%SEMICOLON%") : null;
    }

    /**
     * Replace dedicated unique strings by unescaped chars
     * 
     * @param safeString
     * @return
     */
    private String safeStringToUnescapedCharacters(String safeString) {
        return safeString.replace("%COLON%", ":").replace("%SEMICOLON%", ";");
    }
}
