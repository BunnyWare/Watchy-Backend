package de.fayedev.watchybackend.websocket;

import de.fayedev.watchybackend.model.websocket.SocketMessage;
import de.fayedev.watchybackend.utils.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;

@Controller
@Slf4j
public class WebsocketHandler {

    private final SimpMessagingTemplate template;

    public WebsocketHandler(SimpMessagingTemplate simpMessagingTemplate) {
        this.template = simpMessagingTemplate;
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        var principal = event.getUser();
        var header = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String destination = header.getDestination();
        if (principal != null && destination != null) {
            log.info(LogMessage.WEBSOCKET_CONNECTED_QUEUE, principal.getName(), destination);
        }
    }

    @MessageMapping("/system")
    public void handleIncomingMessages(@Payload SocketMessage message, Principal userPrincipal, @Header("simpSessionId") String sessionId) {
        log.info(LogMessage.WEBSOCKET_MESSAGE_RECEIVED, message.getMessageType(), message.getId(), userPrincipal.getName(), message.getData(), "/pub/system");
    }
}
