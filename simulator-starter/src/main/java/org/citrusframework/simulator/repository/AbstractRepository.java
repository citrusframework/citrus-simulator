package org.citrusframework.simulator.repository;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.Message.Direction;
import org.citrusframework.simulator.model.MessageFilter;
import org.citrusframework.simulator.model.MessageHeader;

public abstract class AbstractRepository {

    /**
     * HeaderFilter should be of format: p1:v1;p2:v2;p3:v3 with p_x being the
     * parameter name and v_x being the parameter value to filter. This pattern is
     * used to divide the filter string into respective tuples.
     */
    private static final Pattern HEADER_FILTER_STRING_PATTERN = Pattern.compile("([^:;]+):([^:;]+);?");

    /**
     * Adds direction predicates if the respective filters are set.
     *
     * @param filter
     * @param criteriaBuilder
     * @param path
     * @param predicates
     */
    protected void addDirectionPredicate(MessageFilter filter, CriteriaBuilder criteriaBuilder, Path<Message> path,
                                         List<Predicate> predicates) {

        if (!filter.getDirectionInbound()) {
            predicates.add(criteriaBuilder.notEqual(path.get("direction"), Direction.INBOUND.getId()));
        }

        if (!filter.getDirectionOutbound()) {
            predicates.add(criteriaBuilder.notEqual(path.get("direction"), Direction.OUTBOUND.getId()));
        }
    }

    /**
     * Join {@link MessageHeader} to the {@link From} using the provided joinFunction.
     *
     * @param <T>
     * @param criteriaBuilder
     * @param headerFilter
     * @param from
     * @param joinFunction
     */
    protected <T> void joinHeader(CriteriaBuilder criteriaBuilder, String headerFilter, From<T, Message> from,
            Function<From<T, Message>, Join<Message, MessageHeader>> joinFunction) {

        if (headerFilter == null) {
            return;
        }

        String escapedFilter = escapedToSafeString(headerFilter);
        Matcher matcher = HEADER_FILTER_STRING_PATTERN.matcher(escapedFilter);

        while (matcher.find()) {

            String headerParam = safeStringToUnescapedCharacters(matcher.group(1));
            String headerValue = safeStringToUnescapedCharacters(matcher.group(2));

            Join<Message, MessageHeader> join = joinFunction.apply(from);
            join.on(criteriaBuilder.and(criteriaBuilder.equal(join.get("name"), headerParam),
                    criteriaBuilder.like(join.get("value"), headerValue)));
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
