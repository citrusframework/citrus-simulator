package com.consol.citrus.simulator.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.consol.citrus.simulator.SimulatorAutoConfiguration;
import com.consol.citrus.simulator.model.Message;
import com.consol.citrus.simulator.model.Message.Direction;
import com.consol.citrus.simulator.repository.MessageRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes= {SimulatorAutoConfiguration.class})
class MessageRepositoryTest {

    private static final String PAYLOAD = "This is a test message!";
	private static final String HEADER_NAME = "h1";

	@Autowired
    private MessageService service;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void testFindByHeader() {
        String uid = createTestMessage();

        List<Message> result =
                        messageRepository
                                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                                        null, null,
                                                        Collections.singleton(Direction.INBOUND),
                                                        null, HEADER_NAME, uid, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(uid, result.get(0).getCitrusMessageId());
        Assertions.assertEquals(PAYLOAD, result.get(0).getPayload());
    }

    @Test
    void testFindByHeaderLike() {
    	String innerUid= UUID.randomUUID().toString();
    	String uid = createTestMessage("PRE"+innerUid+ "POST", PAYLOAD);
        
        List<Message> result =
                        messageRepository
                                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                                        null, null,
                                                        Collections.singleton(Direction.INBOUND),
                                                        null, HEADER_NAME, "%"+innerUid+"%", null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(uid, result.get(0).getCitrusMessageId());
        Assertions.assertEquals(PAYLOAD, result.get(0).getPayload());
    }

    @Test
    void testFindByPayloadLike() {
    	String uid= UUID.randomUUID().toString();
    	String specificPayload = "Pay"+uid+"LOAD";
    	uid = createTestMessage(uid, specificPayload);
        
        List<Message> result =
                        messageRepository
                                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                                        null, null,
                                                        Collections.singleton(Direction.INBOUND),
                                                        "%"+uid+"%", null, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(uid, result.get(0).getCitrusMessageId());
        Assertions.assertEquals(specificPayload, result.get(0).getPayload());
    }
    
    @Test
    void testFindByPayload() {
    	String uid = createTestMessage();
        
        List<Message> result =
                        messageRepository
                                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                                        null, null,
                                                        Collections.singleton(Direction.INBOUND),
                                                        PAYLOAD, null, null, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(uid, result.get(0).getCitrusMessageId());
        Assertions.assertEquals(PAYLOAD, result.get(0).getPayload());
    }

    /**
     * Passing only one parameter of headerParamName and headerParamValue should not perform any filtering
     */
    @Test
    void testFindByHeaderInsufficientParams() {

        String uid = createTestMessage();

        // If headerParamName or headerParamValue is not provided no filtering should be performed
        List<Message> result = messageRepository
                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(null, null,
                                        Collections.singleton(Direction.INBOUND), null, HEADER_NAME, null,
                                        null);

        // No filter thus all messages should be returned. Depending on the number of executed tests or savedMessages this should be a value > 0.
        Assertions.assertTrue(result.size() > 0);

        result = messageRepository.findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                        null, null, Collections.singleton(Direction.INBOUND), null, null, uid,
                        null);

        // No filter thus all messages should be returned. Depending on the number of executed tests or savedMessages this should be a value > 0.
        Assertions.assertTrue(result.size() > 0);

    }


    @Test
    void testFindByAllParams() {
        Date startSavingDate = now();
        String uid = createTestMessage();
        Date endSavingDate = now();

        // Filter by all valid
        List<Message> result = messageRepository
                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                        startSavingDate, endSavingDate,
                                        Collections.singleton(Direction.INBOUND), PAYLOAD, HEADER_NAME,
                                        uid, null);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(PAYLOAD, result.get(0).getPayload());

    }

    @Test
    void testPaging() {
    	String uniquePayload = "PagingPayload"+UUID.randomUUID().toString();
    	for (int i=0;i<100;i++) {
    		createTestMessage(UUID.randomUUID().toString(), uniquePayload);
    	}

        PageRequest pr1 = PageRequest.of(0, 33, Sort.Direction.DESC, "date");
        
        // Filter by all valid
        List<Message> result = messageRepository
                        .findByDateBetweenAndDirectionInAndPayloadContainingIgnoreCase(
                                        null, null,
                                        Collections.singleton(Direction.INBOUND), uniquePayload, HEADER_NAME,
                                        null, pr1);

        Assertions.assertEquals(33, result.size());

    }

	private Date now() {
		return Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC));
	}
    
	private String createTestMessage() {
		return createTestMessage(UUID.randomUUID().toString(), PAYLOAD);
	}
	
	private String createTestMessage(String uid, String payload) {
        		
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put(HEADER_NAME, uid);

        service.saveMessage(Direction.INBOUND, payload, uid, headers);

        return uid;
	}


}
