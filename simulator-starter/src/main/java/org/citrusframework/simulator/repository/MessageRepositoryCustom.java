package org.citrusframework.simulator.repository;

import org.citrusframework.simulator.model.Message;
import org.citrusframework.simulator.model.MessageFilter;

import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> find(MessageFilter filter);
}
