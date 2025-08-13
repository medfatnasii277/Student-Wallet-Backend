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
	        byte[] keyBytes;
	        String trimmed = SECRET.replaceAll("\\s+", "");
	        if (trimmed.matches("(?i)^[0-9a-f]+$")) {
	            keyBytes = hexToBytes(trimmed);
	        } else {
	            keyBytes = Base64.getDecoder().decode(trimmed);
	        }
	        return Keys.hmacShaKeyFor(keyBytes);
	    }
	    
	    private byte[] hexToBytes(String hex) {
	    	int len = hex.length();
	    	byte[] data = new byte[len / 2];
	    	for (int i = 0; i < len; i += 2) {
	    		data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
	    		        + Character.digit(hex.charAt(i+1), 16));
	    	}
	    	return data;
	    }

	    public String extractUsername(String jwt) {
	        Claims claims = getClaims(jwt);
	        return claims.getSubject();
	    }

	    private Claims getClaims(String jwt) {
	        return Jwts.parserBuilder()
	                .setSigningKey(generateKey())
	                .build()
	                .parseClaimsJws(jwt)
	                .getBody();
	    }

	    public boolean isTokenValid(String jwt) {
	        Claims claims = getClaims(jwt);
	        return claims.getExpiration().after(Date.from(Instant.now()));
	    }


}
