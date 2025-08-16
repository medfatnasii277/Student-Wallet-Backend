package StudentWallet.StudentWallet.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageHandler {
    
    /**
     * Handle WebSocket messages sent to /app/notifications
     * This can be used for client-to-server communication if needed
     */
    @MessageMapping("/notifications")
    @SendTo("/topic/notifications")
    public String handleNotificationMessage(String message) {
        // Echo the message back to all subscribers
        return "Server received: " + message;
    }
    
    /**
     * Handle WebSocket messages sent to /app/status
     * This can be used for client status updates
     */
    @MessageMapping("/status")
    @SendTo("/topic/status")
    public String handleStatusMessage(String status) {
        // Broadcast status updates to all subscribers
        return "Status update: " + status;
    }
} 