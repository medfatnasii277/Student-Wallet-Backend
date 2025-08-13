package StudentWallet.StudentWallet.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import StudentWallet.StudentWallet.Dto.ChatDtos.CreateRoomRequest;
import StudentWallet.StudentWallet.Dto.ChatDtos.RoomSummary;
import StudentWallet.StudentWallet.Model.ChatMessage;
import StudentWallet.StudentWallet.Model.ChatRoom;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.ChatMessageRepository;
import StudentWallet.StudentWallet.Repository.ChatRoomRepository;

@Service
public class ChatService {
	private final ChatRoomRepository roomRepo;
	private final ChatMessageRepository messageRepo;
	private final PasswordEncoder passwordEncoder;

	public ChatService(ChatRoomRepository roomRepo, ChatMessageRepository messageRepo, PasswordEncoder passwordEncoder) {
		this.roomRepo = roomRepo;
		this.messageRepo = messageRepo;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public ChatRoom createRoom(CreateRoomRequest request, Student creator) {
		// Check if room with same name already exists for this creator
		List<ChatRoom> existingRooms = roomRepo.findByCreatorAndName(creator, request.name);
		if (!existingRooms.isEmpty()) {
			throw new IllegalArgumentException("You already have a room with this name");
		}
		
		ChatRoom room = new ChatRoom();
		room.setName(request.name);
		room.setDescription(request.description);
		room.setPrivate(request.isPrivate);
		room.setCreator(creator);
		Set<Student> members = new HashSet<>();
		members.add(creator);
		room.setMembers(members);
		if (request.isPrivate) {
			if (request.password == null || request.password.isBlank()) {
				throw new IllegalArgumentException("Password required for private room");
			}
			room.setPasswordHash(passwordEncoder.encode(request.password));
		}
		return roomRepo.save(room);
	}

	@Transactional
	public void joinRoom(Long roomId, Student student, String password) {
		ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));

		// Check if user is already a member
		if (room.getMembers().contains(student)) {
			return; // Already a member, no need to throw error
		}

		if (room.isPrivate()) {
			if (password == null || password.isBlank()) {
				throw new IllegalArgumentException("Password required");
			}
			if (room.getPasswordHash() == null || !passwordEncoder.matches(password, room.getPasswordHash())) {
				throw new IllegalArgumentException("Invalid password");
			}
		}
		
		// Add user to room members
		room.getMembers().add(student);
		roomRepo.save(room);
	}

	@Transactional
	public ChatMessage postMessage(Long roomId, Student sender, String content) {
		ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));
		
		// Check if user is a member of the room
		if (!room.getMembers().contains(sender)) {
			throw new IllegalArgumentException("You must join the room first before sending messages");
		}
		
		ChatMessage msg = new ChatMessage();
		msg.setRoom(room);
		msg.setSender(sender);
		msg.setContent(content);
		ChatMessage saved = messageRepo.save(msg);
		trimToLast50(room);
		return saved;
	}

	@Transactional
	public void trimToLast50(ChatRoom room) {
		long count = messageRepo.countByRoom(room);
		if (count <= 50) return;
		List<ChatMessage> allAsc = messageRepo.findByRoomOrderByCreatedAtAsc(room);
		int toDelete = (int)(count - 50);
		for (int i = 0; i < toDelete && i < allAsc.size(); i++) {
			messageRepo.delete(allAsc.get(i));
		}
	}

	@Transactional(readOnly = true)
	public List<RoomSummary> listRoomsPublicAndMine(Student me) {
		List<RoomSummary> out = new ArrayList<>();

		// Get all public rooms
		for (ChatRoom r : roomRepo.findByIsPrivateFalseOrderByCreatedAtDesc()) {
			out.add(new RoomSummary(r.getId(), r.getName(), r.isPrivate(), r.getCreator().getUsername(), r.getMembers().size()));
		}

		// Get all private rooms (visible to everyone, but require password to join)
		for (ChatRoom r : roomRepo.findByIsPrivateTrueOrderByCreatedAtDesc()) {
			out.add(new RoomSummary(r.getId(), r.getName(), r.isPrivate(), r.getCreator().getUsername(), r.getMembers().size()));
		}

		return out;
	}

	@Transactional(readOnly = true)
	public List<ChatMessage> latest50(Long roomId) {
		ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));
		return messageRepo.findByRoomOrderByCreatedAtDesc(room, PageRequest.of(0, 50));
	}

	@Transactional(readOnly = true)
	public boolean isUserMemberOfRoom(Long roomId, Student student) {
		ChatRoom room = roomRepo.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Room not found"));
		return room.getMembers().contains(student);
	}
} 