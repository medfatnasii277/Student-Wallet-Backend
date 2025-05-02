	package StudentWallet.StudentWallet.Exception;
	
	import java.util.HashMap;
	import java.util.Map;
	
	import org.springframework.http.HttpStatus;
	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.MethodArgumentNotValidException;
	import org.springframework.web.bind.annotation.ExceptionHandler;
	import org.springframework.web.bind.annotation.RestControllerAdvice;
	
	
	import StudentWallet.StudentWallet.Dto.ErrorResponseDTO;
	
	
	@RestControllerAdvice
	public class GlobalExceptionHandler {
		@ExceptionHandler(DocumentNotFoundException.class)
	    public ResponseEntity<ErrorResponseDTO> handleDocumentNotFoundException(DocumentNotFoundException ex) {
	        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    }
	
	    @ExceptionHandler(Exception.class)
	    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
	        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred: " + ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	    
	    @ExceptionHandler(FileStorageException.class)
	    public ResponseEntity<ErrorResponseDTO> handleFileStorageException(FileStorageException ex) {
	        ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	    }
	    
	
	    @ExceptionHandler(StudentNotFoundException.class)
	    public ResponseEntity<ErrorResponseDTO> handleStudentNotFound(StudentNotFoundException ex) {
	    	   ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
	           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    	
	    }
	    
	   
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
	        Map<String, String> errors = new HashMap<>();
	
	        ex.getBindingResult().getFieldErrors().forEach(error -> 
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	
	        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
	    }
	    
	    
	    @ExceptionHandler(InvalidUserFormatException.class)
	    public ResponseEntity<ErrorResponseDTO> handleInvalidUserFormat(InvalidUserFormatException ex) {
	        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
	        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	    }

	    @ExceptionHandler(AuthenticationFailureException.class)
	    public ResponseEntity<ErrorResponseDTO> handleAuthFailure(AuthenticationFailureException ex) {
	        ErrorResponseDTO error = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
	        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	    }
	  
	    
	    
	    
	    
	}
