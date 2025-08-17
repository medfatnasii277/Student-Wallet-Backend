package StudentWallet.StudentWallet.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "document_shares", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"document_id", "recipient_id"})
})
public class DocumentShare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Documents document;
    
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Student owner;
    
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private Student recipient;
    
    @NotNull
    private LocalDateTime sharedAt;
    
    private String message; // Optional message when sharing
    
    // Default constructor
    public DocumentShare() {
        this.sharedAt = LocalDateTime.now();
    }
    
    // Constructor with required fields
    public DocumentShare(Documents document, Student owner, Student recipient) {
        this();
        this.document = document;
        this.owner = owner;
        this.recipient = recipient;
    }
    
    // Constructor with message
    public DocumentShare(Documents document, Student owner, Student recipient, String message) {
        this(document, owner, recipient);
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Documents getDocument() {
        return document;
    }
    
    public void setDocument(Documents document) {
        this.document = document;
    }
    
    public Student getOwner() {
        return owner;
    }
    
    public void setOwner(Student owner) {
        this.owner = owner;
    }
    
    public Student getRecipient() {
        return recipient;
    }
    
    public void setRecipient(Student recipient) {
        this.recipient = recipient;
    }
    
    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
    
    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 