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
import StudentWallet.StudentWallet.Dto.ProfileUpdateDTO;
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

	@Transactional
	public Student updateProfile(String username, ProfileUpdateDTO profileUpdate) {
		Student student = studentRepo.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		// Update only the fields that are provided (not null and not empty)
		if (profileUpdate.getSchool() != null) {
			student.setSchool(profileUpdate.getSchool().trim().isEmpty() ? null : profileUpdate.getSchool().trim());
		}
		if (profileUpdate.getSpecialty() != null) {
			student.setSpecialty(profileUpdate.getSpecialty().trim().isEmpty() ? null : profileUpdate.getSpecialty().trim());
		}
		if (profileUpdate.getInterests() != null) {
			student.setInterests(profileUpdate.getInterests().trim().isEmpty() ? null : profileUpdate.getInterests().trim());
		}
		if (profileUpdate.getBio() != null) {
			student.setBio(profileUpdate.getBio().trim().isEmpty() ? null : profileUpdate.getBio().trim());
		}
		if (profileUpdate.getEmail() != null) {
			student.setEmail(profileUpdate.getEmail().trim().isEmpty() ? null : profileUpdate.getEmail().trim());
		}
		if (profileUpdate.getPhoneNumber() != null) {
			student.setPhoneNumber(profileUpdate.getPhoneNumber().trim().isEmpty() ? null : profileUpdate.getPhoneNumber().trim());
		}
		if (profileUpdate.getYearOfStudy() != null) {
			student.setYearOfStudy(profileUpdate.getYearOfStudy().trim().isEmpty() ? null : profileUpdate.getYearOfStudy().trim());
		}
		if (profileUpdate.getCity() != null) {
			student.setCity(profileUpdate.getCity().trim().isEmpty() ? null : profileUpdate.getCity().trim());
		}
		
		return studentRepo.save(student);
	}

	public Map<String, Object> getFullProfile(String username) throws IOException {
		Student student = studentRepo.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("User not found"));

		Map<String, Object> response = new HashMap<>();
		response.put("id", student.getId());
		response.put("username", student.getUsername());
		response.put("name", student.getName());
		response.put("school", student.getSchool());
		response.put("specialty", student.getSpecialty());
		response.put("interests", student.getInterests());
		response.put("bio", student.getBio());
		response.put("email", student.getEmail());
		response.put("phoneNumber", student.getPhoneNumber());
		response.put("yearOfStudy", student.getYearOfStudy());
		response.put("city", student.getCity());

		// Handle profile picture
		if (student.getProfilePicture() != null && !student.getProfilePicture().isEmpty()) {
			File file = new File(uploadDir + "/" + student.getProfilePicture());
			if (file.exists()) {
				byte[] imageBytes = Files.readAllBytes(file.toPath());
				String base64Image = Base64.getEncoder().encodeToString(imageBytes);
				response.put("profilePicture", base64Image);
			} else {
				response.put("profilePicture", null);
			}
		} else {
			response.put("profilePicture", null);
		}

		return response;
	}

}