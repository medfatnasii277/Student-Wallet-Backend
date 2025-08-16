package StudentWallet.StudentWallet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Notification;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.NotificationRepository;

import java.util.List;

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
        
        // Send real-time notification via WebSocket
        sendNotificationToUser(recipient.getUsername(), notification);
        
        return notification;
    }
    
    /**
     * Create and send a system notification
     */
    public Notification createSystemNotification(Student recipient, String title, String message) {
        Notification notification = new Notification(recipient, title, message);
        notification = notificationRepository.save(notification);
        
        // Send real-time notification via WebSocket
        sendNotificationToUser(recipient.getUsername(), notification);
        
        return notification;
    }
    
    /**
     * Send notification to a specific user via WebSocket
     */
    private void sendNotificationToUser(String username, Notification notification) {
        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/notifications",
            notification
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