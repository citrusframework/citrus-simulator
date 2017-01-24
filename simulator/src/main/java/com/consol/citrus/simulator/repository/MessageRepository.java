package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * JPA repository for {@link Message}
 */
@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByDateBetweenOrderByDateDesc(Date fromDate, Date toDate, Pageable pageable);
}
