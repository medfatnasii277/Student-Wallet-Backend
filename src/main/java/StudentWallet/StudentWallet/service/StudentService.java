package StudentWallet.StudentWallet.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;

@Service
public class StudentService {
	
	
	@Autowired
	MyStudentRepo studentRepo;
	
	
	
	
	
	public List<Documents> getAllStudentDocuments(Long id) {
		return studentRepo.findById(id).get().getDocuments();
	}
	
	
	
	

}
