package StudentWallet.StudentWallet.Model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    @JsonBackReference
    private Student recipient;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonBackReference
    private Student sender;
    
    @ManyToOne
    @JoinColumn(name = "document_id")
    @JsonBackReference
    private Documents document;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @NotNull
    private String title;
    
    @NotNull
    private String message;
    
    @NotNull
    private LocalDateTime createdAt;
    
    private boolean read = false;
    
    private LocalDateTime readAt;
    
    public enum NotificationType {
        DOCUMENT_SHARED,
        DOCUMENT_ACCESSED,
        SYSTEM_MESSAGE
    }
    
    // Default constructor
    public Notification() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Constructor for document shared notification
    public Notification(Student recipient, Student sender, Documents document, String message) {
        this();
        this.recipient = recipient;
        this.sender = sender;
        this.document = document;
        this.type = NotificationType.DOCUMENT_SHARED;
        this.title = "Document Shared";
        this.message = message != null ? message : "A document has been shared with you";
    }
    
    // Constructor for system notifications
    public Notification(Student recipient, String title, String message) {
        this();
        this.recipient = recipient;
        this.type = NotificationType.SYSTEM_MESSAGE;
        this.title = title;
        this.message = message;
    }
    
    // Mark as read
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getRecipient() {
        return recipient;
    }
    
    public void setRecipient(Student recipient) {
        this.recipient = recipient;
    }
    
    public Student getSender() {
        return sender;
    }
    
    public void setSender(Student sender) {
        this.sender = sender;
    }
    
    public Documents getDocument() {
        return document;
    }
    
    public void setDocument(Documents document) {
        this.document = document;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public void setType(NotificationType type) {
        this.type = type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isRead() {
        return read;
    }
    
    public void setRead(boolean read) {
        this.read = read;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
} 