package StudentWallet.StudentWallet.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;

public interface DocumentsRepo  extends JpaRepository<Documents, Long>{
	List<Documents> findByStudent(Student student); 
    Optional<Documents> findById(Long id);

}
