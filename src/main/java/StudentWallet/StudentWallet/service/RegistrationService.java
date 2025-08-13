package StudentWallet.StudentWallet.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import StudentWallet.StudentWallet.Dto.LoginForm;
import StudentWallet.StudentWallet.Exception.AuthenticationFailureException;
import StudentWallet.StudentWallet.Exception.FileStorageException;
import StudentWallet.StudentWallet.Exception.InvalidUserFormatException;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.security.JwtService;
import StudentWallet.StudentWallet.security.MyUserDetailService;
import jakarta.transaction.Transactional;

@Service
public class RegistrationService {

    private final MyStudentRepo studentRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final MyUserDetailService myUserDetailService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public RegistrationService(
        MyStudentRepo studentRepo,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        JwtService jwtService,
        MyUserDetailService myUserDetailService
    ) {
        this.studentRepo = studentRepo;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.myUserDetailService = myUserDetailService;
    }

    @Transactional
    public Student registerUser(String userJson, MultipartFile file) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            Student user = objectMapper.readValue(userJson, Student.class);


            if (user.getName() == null || user.getUsername() == null || user.getPassword() == null) {
                throw new InvalidUserFormatException("Missing required fields");
            }


            if (studentRepo.findByUsername(user.getUsername()).isPresent()) {
                throw new InvalidUserFormatException("Username already exists");
            }

            // Force default role for self-registration
            user.setRole("ROLE_USER");

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            

            if (file != null && !file.isEmpty()) {

                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }


                String originalFilename = file.getOriginalFilename();
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                String filename = UUID.randomUUID().toString() + extension;
                

                Path filePath = uploadPath.resolve(filename);
                Files.copy(file.getInputStream(), filePath);
                

                user.setProfilePicture(filename);
            }


            return studentRepo.save(user);
        } catch (IOException e) {
            throw new InvalidUserFormatException("Invalid user data format", e);
        } catch (Exception e) {
            throw new FileStorageException("Error saving user or file", e);
        }
    }

    public String authenticateUser(LoginForm loginForm) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.username(), loginForm.password())
            );

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(
                    myUserDetailService.loadUserByUsername(loginForm.username())
                );
            } else {
                throw new AuthenticationFailureException("Authentication failed");
            }
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationFailureException("User not found: " + loginForm.username());
        } catch (Exception e) {
            throw new AuthenticationFailureException("Invalid credentials");
        }
    }
}