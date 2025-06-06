package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.Entities.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Messages handleMessage(@Payload Messages message) {
        return message;
    }
}