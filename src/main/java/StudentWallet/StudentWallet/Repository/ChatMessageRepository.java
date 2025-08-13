package StudentWallet.StudentWallet.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import StudentWallet.StudentWallet.Model.ChatMessage;
import StudentWallet.StudentWallet.Model.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findByRoomOrderByCreatedAtDesc(ChatRoom room, Pageable pageable);
	long countByRoom(ChatRoom room);
	List<ChatMessage> findByRoomOrderByCreatedAtAsc(ChatRoom room);
} 