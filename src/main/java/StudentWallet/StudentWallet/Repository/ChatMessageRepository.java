package StudentWallet.StudentWallet.Repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StudentWallet.StudentWallet.Model.ChatMessage;
import StudentWallet.StudentWallet.Model.ChatRoom;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	List<ChatMessage> findByRoomOrderByCreatedAtDesc(ChatRoom room, Pageable pageable);
	long countByRoom(ChatRoom room);
	List<ChatMessage> findByRoomOrderByCreatedAtAsc(ChatRoom room);
	
	@Modifying
	@Query("DELETE FROM ChatMessage m WHERE m.room = :room")
	void deleteByRoom(@Param("room") ChatRoom room);
} 