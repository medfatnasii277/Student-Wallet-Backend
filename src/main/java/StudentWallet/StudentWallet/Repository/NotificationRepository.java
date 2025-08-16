package StudentWallet.StudentWallet.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StudentWallet.StudentWallet.Model.Notification;
import StudentWallet.StudentWallet.Model.Student;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find all notifications for a specific student
    Page<Notification> findByRecipientOrderByCreatedAtDesc(Student recipient, Pageable pageable);
    
    // Find unread notifications for a student
    List<Notification> findByRecipientAndReadOrderByCreatedAtDesc(Student recipient, boolean read);
    
    // Count unread notifications for a student
    long countByRecipientAndRead(Student recipient, boolean read);
    
    // Find notifications by type for a student
    List<Notification> findByRecipientAndTypeOrderByCreatedAtDesc(Student recipient, Notification.NotificationType type);
    
    // Mark all notifications as read for a student
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.recipient = :recipient AND n.read = false")
    void markAllAsRead(@Param("recipient") Student recipient);
    
    // Mark specific notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.read = true, n.readAt = CURRENT_TIMESTAMP WHERE n.id = :id")
    void markAsRead(@Param("id") Long id);
    
    // Delete old read notifications (cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipient = :recipient AND n.read = true AND n.readAt < :cutoffDate")
    void deleteOldReadNotifications(@Param("recipient") Student recipient, @Param("cutoffDate") java.time.LocalDateTime cutoffDate);
} 