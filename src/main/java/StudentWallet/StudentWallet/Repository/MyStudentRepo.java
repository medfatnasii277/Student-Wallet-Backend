package StudentWallet.StudentWallet.Repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import StudentWallet.StudentWallet.Model.Student;


public interface MyStudentRepo extends JpaRepository<Student,Long> {
	
	Optional<Student> findByUsername(String username);

}
