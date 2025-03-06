package StudentWallet.StudentWallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import StudentWallet.StudentWallet.Exception.DocumentNotFoundException;
import StudentWallet.StudentWallet.Model.Documents;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.DocumentsRepo;

@ExtendWith(MockitoExtension.class)
public class DocumentsServiceTest {

    @Mock
    private DocumentsRepo documentsRepo;

    @InjectMocks
    private DocumentsService documentsService;

    private Documents testDocument;
    private final Long testFileId = 1L;


        

    @Test
    void testGetFileById_FileExists() {
    	testDocument = new Documents();
        testDocument.setId(testFileId);
        testDocument.setName("testfile.txt");

        // Mock the repository response for the existing file with ID 1L
        when(documentsRepo.findById(testFileId)).thenReturn(Optional.of(testDocument));
    
    
        // Act
        Documents document = documentsService.getFileById(testFileId);

        // Assert
        assertNotNull(document);
        assertEquals(testFileId, document.getId());
        assertEquals("testfile.txt", document.getName());
    }

    @Test
    void testFilesByStudent() {
        // Arrange
        Student testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("test");

        // Create a mock list of documents
        Documents doc1 = new Documents();
        doc1.setId(101L);
        doc1.setName("file1.txt");
        doc1.setStudent(testStudent);

        Documents doc2 = new Documents();
        doc2.setId(102L);
        doc2.setName("file2.txt");
        doc2.setStudent(testStudent);

        List<Documents> mockDocuments = Arrays.asList(doc1, doc2);

        // Mock repository response
        when(documentsRepo.findByStudent(testStudent)).thenReturn(mockDocuments);

        // Act
        List<Documents> result = documentsService.getFilesByStudent(testStudent);

        // Assert
        assertEquals(2, result.size()); // Expecting two documents
        assertEquals("file1.txt", result.get(0).getName());
        assertEquals("file2.txt", result.get(1).getName());
    }
    
    
    @Test
    void testFilesNotFoundByStudent() {
        // Arrange
        Student testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setName("test");

      
        // Mock repository response
        when(documentsRepo.findByStudent(testStudent)).thenReturn(null);

        // Act
        List<Documents> result = documentsService.getFilesByStudent(testStudent);

        // Assert
       assertNull(result);
    }
    
    
   

}
