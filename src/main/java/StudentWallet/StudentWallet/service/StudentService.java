package StudentWallet.StudentWallet.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import jakarta.transaction.Transactional;

@Service
public class StudentService {

	@Autowired
	private MyStudentRepo studentRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Value("${file.upload-dir}")
	private String uploadDir;


	public Map<String, String> getProfilePicture(String username) throws IOException {

		Student student = studentRepo.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Map<String, String> response = new HashMap<>();
		response.put("username", student.getUsername());


		if (student.getProfilePicture() == null || student.getProfilePicture().isEmpty()) {
			response.put("profilePicture", null);
			return response;
		}


		File file = new File(uploadDir + "/" + student.getProfilePicture());
		byte[] imageBytes = Files.readAllBytes(file.toPath());
		String base64Image = Base64.getEncoder().encodeToString(imageBytes);

		response.put("profilePicture", base64Image);

		return response;
	}


	public Page<Student> getAllStudents(Pageable pageable) {
		return studentRepo.findAll(pageable);
	}


	public Optional<Student> getStudentById(Long id) {
		return studentRepo.findById(id);
	}


	public Optional<Student> getStudentByUsername(String username) {
		return studentRepo.findByUsername(username);
	}


	@Transactional
	public Student saveStudent(Student student) {
		// If this is a new student (no ID), encode the password
		if (student.getId() == null && student.getPassword() != null) {
			student.setPassword(passwordEncoder.encode(student.getPassword()));
		}


		if (student.getRole() == null || student.getRole().isEmpty()) {
			student.setRole("ROLE_USER");
		}

		return studentRepo.save(student);
	}


	public void deleteStudent(Long id) {
		studentRepo.deleteById(id);
	}


	@Transactional
	public Student toggleBan(Long id) {
		Student student = studentRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Student not found with id: " + id));


		if (student.getRole().equals("ROLE_BANNED")) {
			student.setRole("ROLE_USER");
		} else {
			student.setRole("ROLE_BANNED");
		}

		return studentRepo.save(student);
	}


	public String uploadProfilePicture(MultipartFile file) throws IOException {
		// Create directory if it doesn't exist
		Path uploadPath = Paths.get(uploadDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Generate a unique filename
		String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
		Path filePath = uploadPath.resolve(filename);

		// Save the file
		Files.copy(file.getInputStream(), filePath);

		// Return only the filename, not the full path
		return filename;
	}


	@Transactional
	public Student updateStudentWithProfilePicture(Student student, MultipartFile profilePicture) throws IOException {
		if (profilePicture != null && !profilePicture.isEmpty()) {
			String filename = uploadProfilePicture(profilePicture);
			student.setProfilePicture(filename);
		}

		return saveStudent(student);
	}


	public long countStudents() {
		return studentRepo.count();
	}


	public Page<Student> searchStudents(String searchTerm, Pageable pageable) {
		return studentRepo.findByUsernameContainingIgnoreCase(searchTerm, pageable);
	}


}