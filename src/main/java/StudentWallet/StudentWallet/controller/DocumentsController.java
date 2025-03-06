package StudentWallet.StudentWallet.controller;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.DocumentsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

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



@CrossOrigin(origins= "http://localhost:5173")
@RestController
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private MyStudentRepo studentService;

    // Upload a file
    @PostMapping("/upload")
    public ResponseEntity<Documents> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
       Student student = studentService.findByUsername(principal.getName())
    		   .orElseThrow(() -> new RuntimeException("User not found"));
        Documents uploadedDocument = documentsService.uploadFile(file, student);
        return ResponseEntity.ok(uploadedDocument);
    }

 
    @GetMapping("/my-files")
    public ResponseEntity<List<Documents>> getMyFiles(Principal principal) {
        Student student = studentService.findByUsername(principal.getName()).get();
        List<Documents> files = documentsService.getFilesByStudent(student);
        return ResponseEntity.ok(files);
    }
    
    
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource fileResource = documentsService.getFileResource(fileId);

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(documentsService.getFileType(fileId)))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentsService.getFileName(fileId) + "\"")
            .body(fileResource);
    }
    
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        documentsService.deleteFile(id);
        return ResponseEntity.ok("Document with ID " + id + " deleted successfully");
    }
    
    
    
}