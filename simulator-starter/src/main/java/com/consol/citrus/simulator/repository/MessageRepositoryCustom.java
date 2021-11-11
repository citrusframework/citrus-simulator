package com.consol.citrus.simulator.repository;

import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.MessageFilter;
import java.util.List;

public interface MessageRepositoryCustom {
    List<Message> find(MessageFilter filter);
}
