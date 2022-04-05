package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> find(@Param("filter") MessageFilter filter);
}
