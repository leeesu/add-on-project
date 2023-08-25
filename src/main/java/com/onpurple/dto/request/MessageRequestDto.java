package com.project.date.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MessageRequestDto {
    public enum MessageType {
        ENTER, TALK
    }
    private MessageType type;
    private String roomId;
    private String sender;
    private String message;

}
