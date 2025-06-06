package StudentWallet.StudentWallet.Model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

@Entity
public class Student {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Long Id;

    @NotNull(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	String Name;

    @NotNull(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
	String username; 
    
    @NotNull(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$", 
             message = "Password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character")
	String Password;
	
    @NotNull(message = "Role is required")
    @Pattern(regexp = "^(ROLE_USER|ROLE_ADMIN|ROLE_BANNED)$", message = "Invalid role selected")
	private String Role;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	private List<Documents> documents = new ArrayList<>();
    private String ProfilePicture;
    
    @Transient
    private MultipartFile profilePictureFile;
    
    














	public String getProfilePicture() {
		return ProfilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		ProfilePicture = profilePicture;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getRole() {
		return Role;
	}
	public void setRole(String role) {
		Role = role;
	}
	public List<Documents> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Documents> documents) {
		this.documents = documents;
	}
	public MultipartFile getProfilePictureFile() {
		return profilePictureFile;
	}
	public void setProfilePictureFile(MultipartFile profilePictureFile) {
		this.profilePictureFile = profilePictureFile;
	}

}
