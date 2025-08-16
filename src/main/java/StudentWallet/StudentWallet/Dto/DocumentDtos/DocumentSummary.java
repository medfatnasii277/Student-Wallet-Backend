package StudentWallet.StudentWallet.Dto.DocumentDtos;

import java.time.LocalDateTime;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;

public class DocumentSummary {
    
    private Long id;
    private String name;
    private String type;
    private LocalDateTime uploadDate;
    private Student owner;
    private boolean isOwner;
    private boolean isShared;
    private String shareMessage;
    private LocalDateTime sharedAt;
    
    // Constructor for owned documents
    public DocumentSummary(Documents document, boolean isOwner) {
        this.id = document.getId();
        this.name = document.getName();
        this.type = document.getType();
        this.uploadDate = document.getUploadDate();
        this.owner = document.getStudent();
        this.isOwner = isOwner;
        this.isShared = false;
    }
    
    // Constructor for shared documents
    public DocumentSummary(Documents document, boolean isOwner, String shareMessage, LocalDateTime sharedAt) {
        this(document, isOwner);
        this.isShared = true;
        this.shareMessage = shareMessage;
        this.sharedAt = sharedAt;
    }
    
    // Default constructor
    public DocumentSummary() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocalDateTime getUploadDate() {
        return uploadDate;
    }
    
    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
    
    public Student getOwner() {
        return owner;
    }
    
    public void setOwner(Student owner) {
        this.owner = owner;
    }
    
    public boolean isOwner() {
        return isOwner;
    }
    
    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }
    
    public boolean isShared() {
        return isShared;
    }
    
    public void setShared(boolean isShared) {
        this.isShared = isShared;
    }
    
    public String getShareMessage() {
        return shareMessage;
    }
    
    public void setShareMessage(String shareMessage) {
        this.shareMessage = shareMessage;
    }
    
    public LocalDateTime getSharedAt() {
        return sharedAt;
    }
    
    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
} 