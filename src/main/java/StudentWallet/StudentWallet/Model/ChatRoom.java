package StudentWallet.StudentWallet.Model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class ChatRoom {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotNull
	@Size(min = 3, max = 80)
	private String name;

	@Size(max = 500)
	private String description;

	private boolean isPrivate;

	// BCrypt hash when private; null for public rooms
	private String passwordHash;

	@ManyToOne
	@JoinColumn(name = "creator_id", nullable = false)
	private Student creator;

	@ManyToMany
	@JoinTable(
			name = "chatroom_members",
			joinColumns = @JoinColumn(name = "room_id"),
			inverseJoinColumns = @JoinColumn(name = "student_id")
	)
	private Set<Student> members = new HashSet<>();

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChatMessage> messages = new HashSet<>();

	private LocalDateTime createdAt;

	public ChatRoom() {
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setPrivate(boolean aPrivate) {
		isPrivate = aPrivate;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public Student getCreator() {
		return creator;
	}

	public void setCreator(Student creator) {
		this.creator = creator;
	}

	public Set<Student> getMembers() {
		return members;
	}

	public void setMembers(Set<Student> members) {
		this.members = members;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
} 