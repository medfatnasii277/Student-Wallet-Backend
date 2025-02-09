package StudentWallet.StudentWallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import StudentWallet.StudentWallet.Dto.LoginForm;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;
import StudentWallet.StudentWallet.security.JwtService;
import StudentWallet.StudentWallet.security.MyUserDetailService;



@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class RegistrationController {
	
	@Autowired
	MyStudentRepo studentRepo;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailService myUserDetailService;
	
	
	
	 @PostMapping("/register/user")
	    public Student createUser(@RequestBody Student user) {
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        return studentRepo.save(user);
	    }
	 
	 @PostMapping("/authenticate")
	    public String authenticateAndGetToken(@RequestBody LoginForm loginForm) {
	        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
	                loginForm.username(), loginForm.password()
	        ));
	        if (authentication.isAuthenticated()) {
	            return jwtService.generateToken(myUserDetailService.loadUserByUsername(loginForm.username()));
	        } else {
	            throw new UsernameNotFoundException("Invalid credentials");
	        }
	    }
	 
	 
	 
	

}
