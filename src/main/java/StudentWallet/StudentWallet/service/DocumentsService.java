package StudentWallet.StudentWallet.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import StudentWallet.StudentWallet.Exception.DocumentNotFoundException;
import StudentWallet.StudentWallet.Exception.FileStorageException;
import StudentWallet.StudentWallet.Exception.UnauthorizedAccessException;
import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.DocumentsTag;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.DocumentsRepo;
import StudentWallet.StudentWallet.Repository.documentsTagRepository;
import StudentWallet.StudentWallet.Repository.DocumentShareRepository;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepo documentsRepository;

    @Autowired
    private documentsTagRepository tagRepo;

    @Autowired
    private DocumentShareRepository documentShareRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;


    public Documents uploadFile(MultipartFile file, Student student) throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);


        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(uniqueFileName).normalize(); // Normalize path for security


        if (!filePath.getParent().equals(uploadPath.toAbsolutePath())) {
            throw new SecurityException("Invalid file path detected");
        }


        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        Documents document = new Documents();
        document.setName(uniqueFileName);
        document.setType(file.getContentType());
        document.setFilePath(filePath.toString());
        document.setUploadDate(LocalDateTime.now());
        document.setStudent(student);


        DocumentsTag docTag = tagRepo.findByName(file.getContentType());
        if (docTag == null) {
            docTag = new DocumentsTag();
            docTag.setName(file.getContentType());
            tagRepo.save(docTag);
        }
        document.setDocTag(Collections.singletonList(docTag));

        return documentsRepository.save(document);
    }

    // Get files for a student (pagination included)
    public Page<Documents> getFilesByStudent(Student student, Pageable pageable) {
        Page<Documents> pages = documentsRepository.findByStudent(student, pageable);
        if (pages.isEmpty()) {
            throw new DocumentNotFoundException("No files found for this student");
        }
        return pages;
    }

    // Retrieve file by ID
    public Documents getFileById(Long fileId) {
        return documentsRepository.findById(fileId)
                .orElseThrow(() -> new DocumentNotFoundException("Document with ID " + fileId + " not found"));
    }
    
    // Check if a student has access to a document
    public boolean hasAccessToDocument(Long fileId, Student student) {
        Documents document = getFileById(fileId);
        
        // Check if owner
        if (document.getStudent().getId().equals(student.getId())) {
            return true;
        }
        
        // Check if shared
        return documentShareRepository.existsByDocumentAndRecipient(document, student);
    }
    
    // Get file by ID with access control
    public Documents getFileByIdWithAccessControl(Long fileId, Student student) {
        Documents document = getFileById(fileId);
        
        if (!hasAccessToDocument(fileId, student)) {
            throw new UnauthorizedAccessException("You don't have access to this document");
        }
        
        return document;
    }

    // Delete a file from both the database and filesystem
    public void deleteFile(Long id) {
        Documents document = getFileById(id);
        Path filePath = Paths.get(document.getFilePath());

        try {
            Files.deleteIfExists(filePath); // Delete file from disk
        } catch (IOException e) {
            throw new FileStorageException("Error deleting file from disk", e);
        }

        documentsRepository.deleteById(id); // Delete from database
    }

    // Retrieve file resource for downloading
    public Resource getFileResource(Long fileId) {
        Documents document = getFileById(fileId);
        Path filePath = Paths.get(document.getFilePath());

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File cannot be read");
            }
            return resource;
        } catch (Exception e) {
            throw new FileStorageException("Error retrieving file", e);
        }
    }

    // Get file type by ID
    public String getFileType(Long fileId) {
        return documentsRepository.findById(fileId)
                .map(Documents::getType)
                .orElse("application/octet-stream"); // Default type
    }

    // Get file name by ID
    public String getFileName(Long fileId) {
        return documentsRepository.findById(fileId)
                .map(Documents::getName)
                .orElse("unknown_file");
    }
    
    
    
    
    
    
    
    
    
    
    
    
}