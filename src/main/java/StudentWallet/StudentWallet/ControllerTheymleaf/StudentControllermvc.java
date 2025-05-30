package StudentWallet.StudentWallet.ControllerTheymleaf;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.service.DocumentsService;
import StudentWallet.StudentWallet.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentControllermvc {

    @Autowired
    private StudentService studentService;

    @Autowired
    private DocumentsService documentsService;

    // List all students with pagination and sorting
    @GetMapping
    public String listStudents(Model model,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "3") int size,
                               @RequestParam(defaultValue = "id") String sort,
                               @RequestParam(required = false) String search) {

        // Check if user has admin role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        Page<Student> students;
        if (search != null && !search.trim().isEmpty()) {
            // If search parameter is present, use the search method
            students = studentService.searchStudents(search.trim(), PageRequest.of(page, size, Sort.by(sort)));
            model.addAttribute("searchQuery", search.trim());
        } else {
            // Otherwise, get all students
            students = studentService.getAllStudents(PageRequest.of(page, size, Sort.by(sort)));
        }

        model.addAttribute("students", students);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", students.getTotalPages());
        model.addAttribute("sortField", sort);

        return "liste";
    }

    @GetMapping("/new")
    public String showNewStudentForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("isNewStudent", true);
        model.addAttribute("debug", true);
        return "form";
    }

    @PostMapping
    public String createStudent(@Valid @ModelAttribute("student") Student student,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        System.out.println("Creating new student: " + student.getName());

        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("isNewStudent", true);
            model.addAttribute("debug", true);
            return "form";
        }

        try {
            if (student.getProfilePictureFile() != null && !student.getProfilePictureFile().isEmpty()) {
                if (!student.getProfilePictureFile().getContentType().startsWith("image/")) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Only image files are allowed.");
                    model.addAttribute("isNewStudent", true);
                    model.addAttribute("debug", true);
                    return "form";
                }
                studentService.updateStudentWithProfilePicture(student, student.getProfilePictureFile());
            } else {
                studentService.saveStudent(student);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            System.out.println("Error creating student: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            model.addAttribute("isNewStudent", true);
            model.addAttribute("debug", true);
            return "form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        model.addAttribute("student", student);
        model.addAttribute("isNewStudent", false);
        model.addAttribute("debug", true);
        return "form";
    }

    @PostMapping("/update/{id}")
    public String updateStudent(@PathVariable Long id,
                                @Valid @ModelAttribute("student") Student student,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        System.out.println("Updating student with ID: " + id);

        if (result.hasErrors()) {
            System.out.println("Validation errors: " + result.getAllErrors());
            model.addAttribute("isNewStudent", false);
            model.addAttribute("debug", true);
            return "form";
        }

        try {
            student.setId(id);

            if (student.getProfilePictureFile() != null && !student.getProfilePictureFile().isEmpty()) {
                studentService.updateStudentWithProfilePicture(student, student.getProfilePictureFile());
            } else {
                Optional<Student> existingStudent = studentService.getStudentById(id);
                if (existingStudent.isPresent() && (student.getPassword() == null || student.getPassword().isEmpty())) {
                    student.setPassword(existingStudent.get().getPassword());
                }
                studentService.saveStudent(student);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        } catch (Exception e) {
            System.out.println("Error updating student: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating student: " + e.getMessage());
            model.addAttribute("isNewStudent", false);
            model.addAttribute("debug", true);
            return "form";
        }

        return "redirect:/students";
    }

    // Delete student
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/students";
    }

    // ADMIN FEATURES

    // Ban/Unban student
    @GetMapping("/toggle-ban/{id}")
    public String toggleBanStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.toggleBan(id);
            String status = student.getRole().equals("ROLE_BANNED") ? "banned" : "unbanned";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Student " + student.getName() + " has been " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating ban status: " + e.getMessage());
        }
        return "redirect:/students";
    }

    // View student details with documents
    @GetMapping("/details/{id}")
    public String viewStudentDetails(@PathVariable Long id,
                                     Model model,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size) {

        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        // Get documents for this student with pagination
        try {
            Page<Documents> documents = documentsService.getFilesByStudent(student, PageRequest.of(page, size));
            model.addAttribute("documents", documents);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", documents.getTotalPages());
        } catch (Exception e) {
            model.addAttribute("documentError", "No documents found for this student");
        }

        model.addAttribute("student", student);
        return "details";
    }

    // Admin dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        long totalStudents = studentService.countStudents();
        model.addAttribute("totalStudents", totalStudents);

        // Get the latest registered students
        Page<Student> recentStudents = studentService.getAllStudents(
                PageRequest.of(0, 5, Sort.by("id").descending())
        );
        model.addAttribute("recentStudents", recentStudents);

        return "admin/dashboard";
    }

    // Password change form for admin
    @GetMapping("/change-password/{id}")
    public String showChangePasswordForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        model.addAttribute("student", student);
        return "change-password";
    }

    // Process password change
    @PostMapping("/change-password/{id}")
    public String processPasswordChange(@PathVariable Long id,
                                        @RequestParam String newPassword,
                                        @RequestParam String confirmPassword,
                                        RedirectAttributes redirectAttributes) {

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match!");
            return "redirect:/students/change-password/" + id;
        }



        return "redirect:/students";
    }


    @GetMapping("/upload-document/{id}")
    public String showDocumentUploadForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

        model.addAttribute("student", student);
        return "upload-document";
    }


    @PostMapping("/upload-document/{id}")
    public String processDocumentUpload(@PathVariable Long id,
                                        @RequestParam MultipartFile file,
                                        RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a file to upload");
            return "redirect:/students/upload-document/" + id;
        }

        try {
            Student student = studentService.getStudentById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid student Id:" + id));

            documentsService.uploadFile(file, student);
            redirectAttributes.addFlashAttribute("successMessage", "Document uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading document: " + e.getMessage());
        }

        return "redirect:/students/details/" + id;
    }
}