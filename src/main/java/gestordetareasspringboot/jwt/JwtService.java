package gestordetareasspringboot.jwt;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;


@Service
public class JwtService {
	
	@Value("${jwt.secret}")
    String secret;
	
	private Key key;
	
	public JwtService() {}
	
	@PostConstruct
	public void setKey() {
		this.key = Keys.hmacShaKeyFor(this.secret.getBytes());
	}
	
	public String createToken(String username) {
		return Jwts.builder().setSubject(username).setExpiration(new Date()).setExpiration(new Date(System.currentTimeMillis() + 10000 * 1000L)).setNotBefore(new Date()).signWith(this.key).compact();
	}
	
	public String getSubject(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}
	
	public boolean isBearer(String authHeader) {
		return authHeader.startsWith("Bearer ");
	}
	
	public String getToken(String authHeader) {
		return authHeader.substring(7);
	}
	
	public boolean verify(String token, String username, boolean usernameNeeded) {
		try{
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
		}
		catch(JwtException e){
			return false;
		}
		if(usernameNeeded) {
			String subject = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
			if(subject.equals(username)) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}
}
