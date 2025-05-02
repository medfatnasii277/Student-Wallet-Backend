package StudentWallet.StudentWallet.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import StudentWallet.StudentWallet.Model.DocumentsTag;



public interface documentsTagRepository  extends JpaRepository<DocumentsTag,Long>{
	
	public DocumentsTag findByName(String name);

}
