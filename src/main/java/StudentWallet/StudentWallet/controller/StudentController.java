package StudentWallet.StudentWallet.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.service.StudentService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class StudentController {

    @Autowired
    private StudentService studentService;
    
    @Value("${file.upload-dir}") // Configure this in application.properties
    private String uploadDir;

    @GetMapping("/students")
    public String getAllStudents(Model model,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        
        Page<Student> studentPage = studentService.getAllStudents(PageRequest.of(page, size));
        
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalItems", studentPage.getTotalElements());
        
        return "student-list";
    }
    
    @GetMapping("/students/add")
    public String showAddStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }
    
    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute("student") Student student,
                            BindingResult result,
                            @RequestParam("profileImage") MultipartFile profileImage,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        
        if (result.hasErrors()) {
            return "student-form";
        }
        
        try {
            // Create or update the student with profile picture
            studentService.updateStudentWithProfilePicture(student, profileImage);
            redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload profile picture: " + e.getMessage());
        }
        
        return "redirect:/admin/students";
    }
    
    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        model.addAttribute("student", student);
        return "student-form";
    }
    
    @PostMapping("/students/toggle-ban/{id}")
    public String toggleBanStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Student student = studentService.toggleBan(id);
        
        String status = student.getRole().equals("ROLE_BANNED") ? "banned" : "unbanned";
        redirectAttributes.addFlashAttribute("successMessage", 
                "Student " + student.getName() + " has been " + status + ".");
        
        return "redirect:/admin/students";
    }
    
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        return "redirect:/admin/students";
    }
    
    // View student details
    @GetMapping("/students/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        
        model.addAttribute("student", student);
        return "student-details";
    }
    
    @GetMapping("/profile-picture/{filename}")
    @ResponseBody
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String filename) {
        try {
            Path imagePath = Paths.get(uploadDir + filename);
            byte[] imageBytes = Files.readAllBytes(imagePath);
            
            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    private String determineContentType(String filename) {
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        return "application/octet-stream";
    }
    

}