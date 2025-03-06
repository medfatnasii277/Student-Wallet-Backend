package StudentWallet.StudentWallet.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import StudentWallet.StudentWallet.Model.Student;


@ExtendWith(MockitoExtension.class)
public class RegistrationController {

	
	private MockMvc mockMvc ;
	
	
	@InjectMocks
	private RegistrationController registrationController;
	
	
	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build() ;
		
	}
	
	@Test
	public void testAuthenticateValidUser()  {
		
		Student fakeStudent=  new Student();
		fakeStudent.setUsername("test");
		fakeStudent.setPassword("test");
		
		
		
		
	}
	
	
}
