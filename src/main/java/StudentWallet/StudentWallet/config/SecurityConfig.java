package StudentWallet.StudentWallet.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import StudentWallet.StudentWallet.security.JwtAuthenticationFilter;
import StudentWallet.StudentWallet.security.MyUserDetailService;



@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private MyUserDetailService myStudentDetailsService;
	

	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	    return httpSecurity
	            .cors(cors -> cors.configurationSource(request -> {
	                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
	                corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); 
	                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
	                corsConfiguration.setAllowedHeaders(List.of("*"));
	                corsConfiguration.setAllowCredentials(true);
	                return corsConfiguration;
	            }))
	            
	            .authorizeHttpRequests(registry -> {
	                registry.requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll();
	                registry.requestMatchers("/home", "/register/**", "/authenticate/**", "/alldocuments/**").permitAll();
	                registry.requestMatchers("/admin/**").hasRole("ADMIN");
	                registry.requestMatchers("/user/**", "/upload/**", "/download/**").hasRole("USER");
	                registry.anyRequest().authenticated();
	            })
	            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
	            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
	            .build();
	}
    @Bean
    public UserDetailsService userDetailsService() {
        return myStudentDetailsService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(myStudentDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	
	
}
