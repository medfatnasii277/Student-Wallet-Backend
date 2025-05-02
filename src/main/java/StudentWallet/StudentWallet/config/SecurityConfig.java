package StudentWallet.StudentWallet.config;

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
    private MyUserDetailService userDetailService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    // Public resources
                    registry.requestMatchers("/", "/index.html", "/static/**", "/public/**", "/uploads/**",
                            "/css/**", "/js/**", "/images/**", "/webjars/**","/admin/**").permitAll();
                    
                    // Authentication endpoints
                    registry.requestMatchers("/home", "/register/**", "/authenticate", "/ws/**").permitAll();
                    
                    // API endpoints for uploads, downloads, etc.
                    registry.requestMatchers("my-files", "upload/**", "download/**", "delete/**", "/profile-picture").permitAll();
                    
                    // Admin routes
                    //registry.requestMatchers("/admin/**").hasRole("ADMIN");
                    
                    // User routes
                    registry.requestMatchers("/user/**", "/dashboard").hasRole("USER");
                    
                    // Any other request needs authentication
                    registry.anyRequest().authenticated();
                })
               
                
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
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