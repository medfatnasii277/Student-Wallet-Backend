package StudentWallet.StudentWallet.controller;


import java.io.IOException;

import java.security.Principal;

import java.util.Map;


import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;



import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import StudentWallet.StudentWallet.Dto.LoginForm;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.RegistrationService;
import StudentWallet.StudentWallet.service.StudentService;




@Validated

@RestController
public class RegistrationController {

    private final MyStudentRepo studentRepo;
    private final StudentService studentService;
    private final RegistrationService registrationService;

    @Value("${file.upload-dir}") // Configure this in application.properties
    private String uploadDir;

    public RegistrationController(MyStudentRepo studentRepo, 
                                  StudentService studentService, 
                                  RegistrationService registrationService) {
        this.studentRepo = studentRepo;
        this.studentService = studentService;
        this.registrationService = registrationService;
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<Map<String, String>> getProfilePicture(Principal principal) throws IOException {
        return ResponseEntity.ok(studentService.getProfilePicture(principal.getName()));
    }
    
    @GetMapping("/admin")
    public String heyAdmin() {
        return "Hello Admin";
    }
    
    

    @PostMapping("/register/user")
    public ResponseEntity<Student> createUser(@RequestPart("user") String userJson,
                                              @RequestPart(value = "file", required = false) MultipartFile file) {
        
            Student savedUser = registrationService.registerUser(userJson, file);
            return ResponseEntity.ok(savedUser);
       
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody LoginForm loginForm) {
       
            String token = registrationService.authenticateUser(loginForm);
            return ResponseEntity.ok(token);
        
    }

}
