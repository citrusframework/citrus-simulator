package com.consol.citrus.simulator.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * JPA entity for representing inbound and outbound messages
 */
@Entity
public class Message implements Serializable {
    public enum Direction {
        INBOUND,
        OUTBOUND
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @Column(nullable = false)
    private Direction direction;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(columnDefinition = "CLOB")
    @Lob
    private String payload;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "date=" + date +
                ", messageId=" + messageId +
                ", direction=" + direction +
                ", payload='" + payload + '\'' +
                '}';
    }
}
