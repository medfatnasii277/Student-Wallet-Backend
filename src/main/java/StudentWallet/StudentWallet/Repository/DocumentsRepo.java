package StudentWallet.StudentWallet.Repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;

public interface DocumentsRepo  extends JpaRepository<Documents, Long>{
	
	
	Page<Documents> findByStudent(Student student, Pageable pageable);
    Optional<Documents> findById(Long id);

}
