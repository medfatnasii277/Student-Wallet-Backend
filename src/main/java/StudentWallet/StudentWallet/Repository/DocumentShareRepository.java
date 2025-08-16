package StudentWallet.StudentWallet.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StudentWallet.StudentWallet.Model.DocumentShare;
import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;

public interface DocumentShareRepository extends JpaRepository<DocumentShare, Long> {
    
    // Find all shares for a specific document
    List<DocumentShare> findByDocument(Documents document);
    
    // Find all shares where a student is the recipient
    List<DocumentShare> findByRecipient(Student recipient);
    
    // Find all shares where a student is the owner
    List<DocumentShare> findByOwner(Student owner);
    
    // Find specific share by document and recipient
    Optional<DocumentShare> findByDocumentAndRecipient(Documents document, Student recipient);
    
    // Check if a document is shared with a specific student
    boolean existsByDocumentAndRecipient(Documents document, Student recipient);
    
    // Find all documents shared with a student (for access control)
    @Query("SELECT ds.document FROM DocumentShare ds WHERE ds.recipient = :recipient")
    List<Documents> findSharedDocumentsByRecipient(@Param("recipient") Student recipient);
    
    // Find all shares for a document with recipient details
    @Query("SELECT ds FROM DocumentShare ds WHERE ds.document = :document")
    List<DocumentShare> findSharesByDocumentWithDetails(@Param("document") Documents document);
    
    // Delete all shares for a specific document (when document is deleted)
    void deleteByDocument(Documents document);
    
    // Delete specific share
    void deleteByDocumentAndRecipient(Documents document, Student recipient);
} 