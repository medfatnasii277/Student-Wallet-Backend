package StudentWallet.StudentWallet.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import StudentWallet.StudentWallet.Dto.DocumentDtos.DocumentSummary;
import StudentWallet.StudentWallet.Dto.DocumentDtos.ShareDocumentRequest;
import StudentWallet.StudentWallet.Model.DocumentShare;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.DocumentShareService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/documents")
public class DocumentShareController {
    
    private final DocumentShareService documentShareService;
    private final MyStudentRepo studentRepository;
    
    @Autowired
    public DocumentShareController(DocumentShareService documentShareService, MyStudentRepo studentRepository) {
        this.documentShareService = documentShareService;
        this.studentRepository = studentRepository;
    }
    
    /**
     * Share a document with another student
     */
    @PostMapping("/{documentId}/share")
    public ResponseEntity<DocumentShare> shareDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody ShareDocumentRequest request,
            Principal principal) {
        
        Student owner = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        DocumentShare share = documentShareService.shareDocument(
            documentId, 
            request.getRecipientEmail(), 
            request.getMessage(), 
            owner
        );
        
        return ResponseEntity.ok(share);
    }
    
    /**
     * Get all documents accessible to the current user (owned + shared)
     */
    @GetMapping("/accessible")
    public ResponseEntity<List<DocumentSummary>> getAccessibleDocuments(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DocumentSummary> documents = documentShareService.getAccessibleDocuments(student);
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Get all students who have access to a specific document
     */
    @GetMapping("/{documentId}/recipients")
    public ResponseEntity<List<Student>> getDocumentRecipients(
            @PathVariable Long documentId,
            Principal principal) {
        
        Student owner = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Student> recipients = documentShareService.getDocumentRecipients(documentId, owner);
        return ResponseEntity.ok(recipients);
    }
    
    /**
     * Revoke access to a document for a specific student
     */
    @DeleteMapping("/{documentId}/share")
    public ResponseEntity<Void> revokeAccess(
            @PathVariable Long documentId,
            @RequestBody ShareDocumentRequest request,
            Principal principal) {
        
        Student owner = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        documentShareService.revokeAccess(documentId, request.getRecipientEmail(), owner);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all documents shared by the current user
     */
    @GetMapping("/shared-by-me")
    public ResponseEntity<List<DocumentShare>> getDocumentsSharedByMe(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DocumentShare> shares = documentShareService.getDocumentsSharedByStudent(student);
        return ResponseEntity.ok(shares);
    }
    
    /**
     * Get all documents shared with the current user
     */
    @GetMapping("/shared-with-me")
    public ResponseEntity<List<DocumentShare>> getDocumentsSharedWithMe(Principal principal) {
        Student student = studentRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<DocumentShare> shares = documentShareService.getDocumentsSharedWithStudent(student);
        return ResponseEntity.ok(shares);
    }
} 