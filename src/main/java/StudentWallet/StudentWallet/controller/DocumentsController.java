package StudentWallet.StudentWallet.controller;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.DocumentsService;
import StudentWallet.StudentWallet.service.StudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private MyStudentRepo studentService;

    // Upload a file
    @PostMapping("/upload")
    public ResponseEntity<Documents> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
       Student student = studentService.findByUsername(principal.getName()).get(); // Get the logged-in student
        Documents uploadedDocument = documentsService.uploadFile(file, student);
        return ResponseEntity.ok(uploadedDocument);
    }

    // Get all files for the logged-in student
    @GetMapping("/my-files")
    public ResponseEntity<List<Documents>> getMyFiles(Principal principal) {
        Student student = studentService.findByUsername(principal.getName()).get(); // Get the logged-in student
        List<Documents> files = documentsService.getFilesByStudent(student);
        return ResponseEntity.ok(files);
    }
    
    
    // Download a file by its ID
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        // Fetch the file metadata from the database
        Documents document = documentsService.getFileById(fileId);

        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        // Get the file path
        Path filePath = Paths.get(document.getFilePath());

        try {
            // Load the file as a Resource
            Resource resource = new UrlResource(filePath.toUri());

            // Check if the file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                // Set the content type and attachment header
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getName() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    
    
    
    
    
    
}