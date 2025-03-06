package StudentWallet.StudentWallet.security;

import static io.jsonwebtoken.Jwts.builder;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
		
		@Value("${SECRET}")
		private String SECRET;
		
	
	    private static final long VALIDITY = TimeUnit.MINUTES.toMillis(30);

	    public String generateToken(UserDetails userDetails) {
	        Map<String, String> claims = new HashMap<>();

	        return builder()
	                .setClaims(claims)
	                .setSubject(userDetails.getUsername())
	                .setIssuedAt(Date.from(Instant.now()))
	                .setExpiration(Date.from(Instant.now().plusMillis(VALIDITY)))
	                .signWith(generateKey())
	                .compact();
	    }

	    private SecretKey generateKey() {
	        byte[] decodedKey = Base64.getDecoder().decode(SECRET);
	        return Keys.hmacShaKeyFor(decodedKey);
	    }

	    public String extractUsername(String jwt) {
	        Claims claims = getClaims(jwt);
	        return claims.getSubject();
	    }

	    private Claims getClaims(String jwt) {
	        return Jwts.parserBuilder() // Create a JwtParserBuilder
	                .setSigningKey(generateKey()) // Set the signing key for verification
	                .build() // Build the parser
	                .parseClaimsJws(jwt) // Parse the token and validate the signature
	                .getBody(); // Extract the claims (payload) from the token
	    }

	    public boolean isTokenValid(String jwt) {
	        Claims claims = getClaims(jwt);
	        return claims.getExpiration().after(Date.from(Instant.now()));
	    }


}
