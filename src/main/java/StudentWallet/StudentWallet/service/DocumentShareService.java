package StudentWallet.StudentWallet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import StudentWallet.StudentWallet.Dto.DocumentDtos.DocumentSummary;
import StudentWallet.StudentWallet.Exception.DocumentNotFoundException;
import StudentWallet.StudentWallet.Exception.UnauthorizedAccessException;
import StudentWallet.StudentWallet.Model.DocumentShare;
import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.DocumentShareRepository;
import StudentWallet.StudentWallet.Repository.DocumentsRepo;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;

@Service
@Transactional
public class DocumentShareService {
    
    @Autowired
    private DocumentShareRepository documentShareRepository;
    
    @Autowired
    private DocumentsRepo documentsRepository;
    
    @Autowired
    private MyStudentRepo studentRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Share a document with another student by email
     */
    public DocumentShare shareDocument(Long documentId, String recipientEmail, String message, Student owner) {
        // Verify the document exists and belongs to the owner
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        
        if (!document.getStudent().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You can only share your own documents");
        }
        
        // Find the recipient student by email
        Student recipient = studentRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student with email " + recipientEmail + " not found"));
        
        // Check if already shared
        if (documentShareRepository.existsByDocumentAndRecipient(document, recipient)) {
            throw new IllegalArgumentException("Document is already shared with this student");
        }
        
        // Create the share
        DocumentShare share = new DocumentShare(document, owner, recipient, message);
        share = documentShareRepository.save(share);
        
        // Send notification
        notificationService.createDocumentSharedNotification(recipient, owner, document, message);
        
        return share;
    }
    
    /**
     * Get all documents accessible to a student (owned + shared)
     */
    public List<DocumentSummary> getAccessibleDocuments(Student student) {
        List<DocumentSummary> documents = new java.util.ArrayList<>();
        
        // Add owned documents
        List<Documents> ownedDocs = student.getDocuments();
        for (Documents doc : ownedDocs) {
            documents.add(new DocumentSummary(doc, true));
        }
        
        // Add shared documents
        List<DocumentShare> sharedShares = documentShareRepository.findByRecipient(student);
        for (DocumentShare share : sharedShares) {
            Documents doc = share.getDocument();
            documents.add(new DocumentSummary(doc, false, share.getMessage(), share.getSharedAt()));
        }
        
        return documents;
    }
    
    /**
     * Check if a student has access to a document
     */
    public boolean hasAccessToDocument(Long documentId, Student student) {
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        
        // Check if owner
        if (document.getStudent().getId().equals(student.getId())) {
            return true;
        }
        
        // Check if shared
        return documentShareRepository.existsByDocumentAndRecipient(document, student);
    }
    
    /**
     * Get all students who have access to a document
     */
    public List<Student> getDocumentRecipients(Long documentId, Student owner) {
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        
        if (!document.getStudent().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You can only view recipients of your own documents");
        }
        
        List<DocumentShare> shares = documentShareRepository.findByDocument(document);
        return shares.stream()
                .map(DocumentShare::getRecipient)
                .collect(Collectors.toList());
    }
    
    /**
     * Revoke access to a document for a specific student
     */
    public void revokeAccess(Long documentId, String recipientEmail, Student owner) {
        Documents document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));
        
        if (!document.getStudent().getId().equals(owner.getId())) {
            throw new UnauthorizedAccessException("You can only revoke access to your own documents");
        }
        
        Student recipient = studentRepository.findByEmail(recipientEmail)
                .orElseThrow(() -> new IllegalArgumentException("Student with email " + recipientEmail + " not found"));
        
        documentShareRepository.deleteByDocumentAndRecipient(document, recipient);
        
        // Send notification about access revocation
        notificationService.createSystemNotification(recipient, 
            "Document Access Revoked", 
            "Your access to document '" + document.getName() + "' has been revoked.");
    }
    
    /**
     * Get all documents shared by a student
     */
    public List<DocumentShare> getDocumentsSharedByStudent(Student student) {
        return documentShareRepository.findByOwner(student);
    }
    
    /**
     * Get all documents shared with a student
     */
    public List<DocumentShare> getDocumentsSharedWithStudent(Student student) {
        return documentShareRepository.findByRecipient(student);
    }
} 