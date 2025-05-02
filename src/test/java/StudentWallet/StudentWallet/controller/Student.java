package StudentWallet.StudentWallet.controller;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity

public class Student {
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	Long Id;

    @NotNull
    @Size(min = 3, message = "Student name must have at least 3 characters.")
	String Name;

    @NotNull
    @Size(min=3)
	String username; 
    
    
	String Password;
	
	private String Role;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
	private List<Documents> documents = new ArrayList<>();
    private String ProfilePicture;
    
    

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
	

}
