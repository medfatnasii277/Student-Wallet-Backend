package StudentWallet.StudentWallet.Repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import StudentWallet.StudentWallet.Model.Student;

public interface MyStudentRepo extends JpaRepository<Student,Long> {
	
	Optional<Student> findByUsername(String username);
    Page<Student> findAll(Pageable pageable);


    Page<Student> findByUsernameContainingIgnoreCase(String username, Pageable pageable);
}
