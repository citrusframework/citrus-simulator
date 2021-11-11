package com.consol.citrus.simulator.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Pageable;
import com.consol.citrus.simulator.model.Message;

public interface MessageRepositoryCustom {
    List<Message> findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(Date fromDate,
                    Date toDate, Collection<Message.Direction> directions, String containingText,
                    String headerParameterName, String headerParameterValue, Pageable pageable);
}
