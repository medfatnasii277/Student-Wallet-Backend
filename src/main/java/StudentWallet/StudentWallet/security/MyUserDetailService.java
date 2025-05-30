package StudentWallet.StudentWallet.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import StudentWallet.StudentWallet.Model.Student;
import StudentWallet.StudentWallet.Repository.MyStudentRepo;

@Service
public class MyUserDetailService implements UserDetailsService {
    
    @Autowired
    MyStudentRepo studentRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Student> user = studentRepo.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(getRoles(userObj))
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String[] getRoles(Student user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            return new String[]{"USER"};
        }
        // Remove ROLE_ prefix for Spring Security roles
        String role = user.getRole();
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }
        return new String[]{role};
    }
}
	
	
	
	
	
	


