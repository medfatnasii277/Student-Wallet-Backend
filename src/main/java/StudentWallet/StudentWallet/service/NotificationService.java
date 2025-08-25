package StudentWallet.StudentWallet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Notification;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.NotificationRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Create and send a document shared notification
     */
    public Notification createDocumentSharedNotification(Student recipient, Student sender, Documents document, String message) {
        Notification notification = new Notification(recipient, sender, document, message);
        notification = notificationRepository.save(notification);
        
    // Send real-time notification via WebSocket using a lightweight DTO
    sendNotificationToUser(recipient.getUsername(), notification);
        
        return notification;
    }
    
    /**
     * Create and send a system notification
     */
    public Notification createSystemNotification(Student recipient, String title, String message) {
        Notification notification = new Notification(recipient, title, message);
        notification = notificationRepository.save(notification);
        
    // Send real-time notification via WebSocket using a lightweight DTO
    sendNotificationToUser(recipient.getUsername(), notification);
        
        return notification;
    }
    
    /**
     * Send notification to a specific user via WebSocket
     */
    private void sendNotificationToUser(String username, Notification notification) {
        // Build a small DTO to avoid JPA serialization issues across WebSocket
        Map<String, Object> dto = new HashMap<>();
        dto.put("id", notification.getId());
        dto.put("type", notification.getType() != null ? notification.getType().name() : null);
        dto.put("title", notification.getTitle());
        dto.put("message", notification.getMessage());
        dto.put("createdAt", notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null);
        dto.put("read", notification.isRead());

        if (notification.getSender() != null) {
            Map<String, Object> sender = new HashMap<>();
            sender.put("id", notification.getSender().getId());
            sender.put("username", notification.getSender().getUsername());
            sender.put("name", notification.getSender().getName());
            dto.put("sender", sender);
        }

        if (notification.getDocument() != null) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", notification.getDocument().getId());
            doc.put("name", notification.getDocument().getName());
            dto.put("document", doc);
        }

        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/notifications",
            dto
        );
    }
    
    /**
     * Get all notifications for a student
     */
    public List<Notification> getNotificationsForStudent(Student student) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(student, 
            org.springframework.data.domain.PageRequest.of(0, 50)).getContent();
    }
    
    /**
     * Get unread notifications for a student
     */
    public List<Notification> getUnreadNotificationsForStudent(Student student) {
        return notificationRepository.findByRecipientAndReadOrderByCreatedAtDesc(student, false);
    }
    
    /**
     * Mark a notification as read
     */
    @org.springframework.transaction.annotation.Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }
    
    /**
     * Mark all notifications as read for a student
     */
    public void markAllNotificationsAsRead(Student student) {
        notificationRepository.markAllAsRead(student);
    }
    
    /**
     * Get unread notification count for a student
     */
    public long getUnreadNotificationCount(Student student) {
        return notificationRepository.countByRecipientAndRead(student, false);
    }
} 