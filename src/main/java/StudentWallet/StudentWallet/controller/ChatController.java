package StudentWallet.StudentWallet.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import StudentWallet.StudentWallet.Dto.ChatDtos.CreateRoomRequest;
import StudentWallet.StudentWallet.Dto.ChatDtos.JoinRoomRequest;
import StudentWallet.StudentWallet.Dto.ChatDtos.PostMessageRequest;
import StudentWallet.StudentWallet.Dto.ChatDtos.RoomSummary;
import StudentWallet.StudentWallet.Model.ChatMessage;
import StudentWallet.StudentWallet.Model.ChatRoom;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.service.ChatService;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/rooms")
public class ChatController {
	private final ChatService chatService;
	private final MyStudentRepo studentRepo;

	public ChatController(ChatService chatService, MyStudentRepo studentRepo) {
		this.chatService = chatService;
		this.studentRepo = studentRepo;
	}

	@PostMapping
	public ResponseEntity<ChatRoom> create(@Valid @RequestBody CreateRoomRequest request, Principal principal) {
		Student me = studentRepo.findByUsername(principal.getName()).orElseThrow();
		return ResponseEntity.ok(chatService.createRoom(request, me));
	}

	@PostMapping("/{roomId}/join")
	public ResponseEntity<Void> join(@PathVariable Long roomId, @RequestBody(required = false) JoinRoomRequest req, Principal principal) {
		Student me = studentRepo.findByUsername(principal.getName()).orElseThrow();
		chatService.joinRoom(roomId, me, req == null ? null : req.password);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{roomId}/messages")
	public ResponseEntity<ChatMessage> post(@PathVariable Long roomId, @Valid @RequestBody PostMessageRequest req, Principal principal) {
		Student me = studentRepo.findByUsername(principal.getName()).orElseThrow();
		return ResponseEntity.ok(chatService.postMessage(roomId, me, req.content));
	}

	@GetMapping
	public ResponseEntity<List<RoomSummary>> list(Principal principal) {
		Student me = studentRepo.findByUsername(principal.getName()).orElseThrow();
		return ResponseEntity.ok(chatService.listRoomsPublicAndMine(me));
	}

	@GetMapping("/{roomId}/messages/latest")
	public ResponseEntity<List<ChatMessage>> latest(@PathVariable Long roomId) {
		return ResponseEntity.ok(chatService.latest50(roomId));
	}
} 