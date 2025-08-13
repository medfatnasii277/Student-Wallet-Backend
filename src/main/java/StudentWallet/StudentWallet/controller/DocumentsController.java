package StudentWallet.StudentWallet.controller;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.DocumentsService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;

import java.security.Principal;


import org.springframework.core.io.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;




@RestController
public class DocumentsController {

    private final DocumentsService documentsService;
    private final MyStudentRepo studentService;

    @Autowired
    public DocumentsController(DocumentsService documentsService, MyStudentRepo studentService) {
        this.documentsService = documentsService;
        this.studentService = studentService;
    }


    @PostMapping("/upload")
    public ResponseEntity<Documents> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) throws IOException {
        Student student = studentService.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Documents uploadedDocument = documentsService.uploadFile(file, student);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedDocument);
    }


    @GetMapping("/my-files")
    public ResponseEntity<Page<Documents>> getMyFiles(
            Principal principal,
            @RequestParam(defaultValue  = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        
        Student student = studentService.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Documents> files = documentsService.getFilesByStudent(student, pageable);

        return ResponseEntity.ok(files);
    }


    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        Resource fileResource = documentsService.getFileResource(fileId);
        String contentType = documentsService.getFileType(fileId);
        String fileName = documentsService.getFileName(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(fileResource);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        documentsService.deleteFile(id);
        return ResponseEntity.noContent().build(); // 204 No Content for successful deletion
    }
    
}