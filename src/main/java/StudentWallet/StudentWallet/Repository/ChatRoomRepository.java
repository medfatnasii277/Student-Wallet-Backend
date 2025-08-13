package StudentWallet.StudentWallet.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import StudentWallet.StudentWallet.Model.ChatRoom;
import StudentWallet.StudentWallet.Model.Student;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
        List<ChatRoom> findByIsPrivateFalseOrderByCreatedAtDesc();
        List<ChatRoom> findByIsPrivateTrueOrderByCreatedAtDesc();
        List<ChatRoom> findByCreatorOrderByCreatedAtDesc(Student creator);
        List<ChatRoom> findByCreatorAndName(Student creator, String name);
        Optional<ChatRoom> findById(Long id);

        @Query("SELECT DISTINCT r FROM ChatRoom r JOIN r.members m WHERE m = :student AND r.creator != :student")
        List<ChatRoom> findRoomsWhereUserIsMemberButNotCreator(@Param("student") Student student);
} 