package StudentWallet.StudentWallet.service;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.DocumentsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepo documentsRepository;

    @Autowired
    private StudentService studentService; // To fetch the logged-in student

    @Value("${file.upload-dir}") // Configure this in application.properties
    private String uploadDir;

    // Upload a file
    public Documents uploadFile(MultipartFile file, Student student) throws IOException {
        // Ensure the upload directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate a unique file name to avoid conflicts
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = uploadDir + "/" + uniqueFileName;

        // Save the file to the server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Save file metadata to the database
        Documents document = new Documents();
        document.setName(file.getOriginalFilename());
        document.setType(file.getContentType());
        document.setFilePath(filePath);
        document.setUploadDate(LocalDateTime.now());
        document.setStudent(student);

        return documentsRepository.save(document);
    }

    // Get all files for a specific student
    public List<Documents> getFilesByStudent(Student student) {
        return documentsRepository.findByStudent(student);
    }
    
    
    
    // Get a file by its ID
    public Documents getFileById(Long fileId) {
        Optional<Documents> document = documentsRepository.findById(fileId);
        return document.orElse(null);
    }
    
    
    
}