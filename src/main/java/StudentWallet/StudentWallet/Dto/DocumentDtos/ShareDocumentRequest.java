package StudentWallet.StudentWallet.Dto.DocumentDtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ShareDocumentRequest {
    
    @NotNull(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String recipientEmail;
    
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
    
    // Default constructor
    public ShareDocumentRequest() {}
    
    // Constructor with required fields
    public ShareDocumentRequest(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    // Constructor with all fields
    public ShareDocumentRequest(String recipientEmail, String message) {
        this.recipientEmail = recipientEmail;
        this.message = message;
    }
    
    // Getters and Setters
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
} 