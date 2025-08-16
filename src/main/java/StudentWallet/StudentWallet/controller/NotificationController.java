package StudentWallet.StudentWallet.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import StudentWallet.StudentWallet.Model.Notification;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    
    private final NotificationService notificationService;
    private final MyStudentRepo studentRepository;
    
    @Autowired
    public NotificationController(NotificationService notificationService, MyStudentRepo studentRepository) {
        this.notificationService = notificationService;
        this.studentRepository = studentRepository;
    }
    
    /**
     * Get all notifications for the current user
     */
    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationService.getNotificationsForStudent(student);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Get unread notifications for the current user
     */
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Notification> notifications = notificationService.getUnreadNotificationsForStudent(student);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Get unread notification count for the current user
     */
    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadNotificationCount(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        long count = notificationService.getUnreadNotificationCount(student);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Mark a specific notification as read
     */
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            Principal principal) {
        
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Mark all notifications as read for the current user
     */
    @PostMapping("/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        notificationService.markAllNotificationsAsRead(student);
        return ResponseEntity.ok().build();
    }
} 