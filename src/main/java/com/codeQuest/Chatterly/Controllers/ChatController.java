package com.codeQuest.Chatterly.Controllers;

import com.codeQuest.Chatterly.Entities.Message;
import com.codeQuest.Chatterly.Enums.MessageType;
import com.codeQuest.Chatterly.Repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload Message message,
                                 @Header("simpSessionId") String sessionId) {
        message.setType(MessageType.PRIVATE);
        message.setSentAt(LocalDateTime.parse(String.valueOf(LocalDateTime.now())));
        messageRepository.save(message);
        
        // Send to sender and receiver
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getId()), "/queue/private", message);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getReceiverId()), "/queue/private", message);
    }

    @MessageMapping("/chat.group")
    public void sendGroupMessage(@Payload Message message) {
        message.setType(MessageType.GROUP);
        message.setSentAt(LocalDateTime.parse(String.valueOf(LocalDateTime.now())));
        messageRepository.save(message);
        
        messagingTemplate.convertAndSend("/topic/group." + message.getGroupId(), message);
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Long messageId, 
                          @Header("simpSessionId") String sessionId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setRead(true);
            messageRepository.save(message);
        });
    }


    @MessageMapping("/chat.edit")
    public void editMessage(@Payload Message message) {
        messageRepository.findById(message.getId()).ifPresent(existingMessage -> {
            existingMessage.setContent(message.getContent());
            existingMessage.setEdited(true);
            messageRepository.save(existingMessage);
            
            messagingTemplate.convertAndSend("/topic/group." + existingMessage.getGroupId(), existingMessage);
        });
    }


    @MessageMapping("/chat.delete")
    public void deleteMessage(@Payload Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            messageRepository.delete(message);
            
            messagingTemplate.convertAndSend("/topic/group." + message.getGroupId(), 
                                             "Message deleted: " + messageId);
        });
    }

    @MessageMapping("/chat.typing")
    public void typingNotification(@Payload String groupId, 
                                  @Header("simpSessionId") String sessionId) {
        messagingTemplate.convertAndSend("/topic/group." + groupId, 
                                         "User is typing...");
    }
}