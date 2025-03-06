package StudentWallet.StudentWallet.service;

import StudentWallet.StudentWallet.Exception.DocumentNotFoundException;
import StudentWallet.StudentWallet.Exception.FileStorageException;
import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.DocumentsRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepo documentsRepository;

  

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
        
        var targetFile = new File(uploadDir+"/"+file.getOriginalFilename());
        if(!Objects.equals(targetFile.getParent(), uploadDir)) {
        	throw new SecurityException("Invalid File name");
        }
        
        

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
    	List<Documents> liste ;
        		liste = documentsRepository.findByStudent(student);
        		if(liste == null) {
        			throw new DocumentNotFoundException("FIle not found");
        		}
        		return  liste ;
        		
    }
    
    
    
    // Get a file by its ID
    public Documents getFileById(Long fileId) {
        return documentsRepository.findById(fileId)
                .orElseThrow(() -> new DocumentNotFoundException("Document with ID " + fileId + " not found"));
    }

    
    public void deleteFile(Long id) {
        if (!documentsRepository.existsById(id)) {
            throw new DocumentNotFoundException("Document with ID " + id + " not found");
        }
        documentsRepository.deleteById(id);
    }
    
    
    
    
    public Resource getFileResource(Long fileId) {
        Documents document = documentsRepository.findById(fileId)
            .orElseThrow(() -> new DocumentNotFoundException("Document with ID " + fileId + " not found"));

        Path filePath = Paths.get(document.getFilePath());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileStorageException("File cannot be read");
            }
            return resource;
        } catch (Exception e) {
            throw new FileStorageException("Error while retrieving file", e);
        }
    }

    public String getFileType(Long fileId) {
        return documentsRepository.findById(fileId)
            .map(Documents::getType)
            .orElse("application/octet-stream"); // Default type if not found
    }

    public String getFileName(Long fileId) {
        return documentsRepository.findById(fileId)
            .map(Documents::getName)
            .orElse("unknown_file");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}