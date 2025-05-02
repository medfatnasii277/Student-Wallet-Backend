package StudentWallet.StudentWallet.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;
    
    
    @ManyToMany
    private List<DocumentsTag> docTag = new ArrayList<>();
    
    

    
    
    
    
    
    
    
    

    @NotNull
    @Size(min=3)
	private String name; // Original file name
    private String type; // File type (e.g., txt, pdf, etc.)
    private String filePath; // Path to the file on the server
    private LocalDateTime uploadDate; // Timestamp of upload


    
    
    
    

 

	public List<DocumentsTag> getDocTag() {
		return docTag;
	}

	public void setDocTag(List<DocumentsTag> docTag) {
		this.docTag = docTag;
	}

	// Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}