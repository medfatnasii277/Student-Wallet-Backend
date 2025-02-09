package StudentWallet.StudentWallet.security;

import static io.jsonwebtoken.Jwts.builder;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	   private static final String SECRET = "638CBE3A90E0303BF3808F40F95A7F02A24B4B5D029C954CF553F79E9EF1DC0384BE681C249F1223F6B55AA21DC070914834CA22C8DD98E14A872CA010091ACC";
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
