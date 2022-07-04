package gestordetareasspringboot.rest;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import gestordetareasspringboot.dto.SimpleUserDTO;
import gestordetareasspringboot.dto.UserDTO;
import gestordetareasspringboot.user.User;
import gestordetareasspringboot.user.UserRepository;

@RestController
public class UserRestController {
	@Autowired
	private UserRepository userRepo;
	
	@PostMapping("/sign-up")
	public ResponseEntity<Object> signup(@RequestBody SimpleUserDTO userInfo){
		if(userInfo.getName().isBlank() || userInfo.getPassword().isBlank() || userInfo.getEmail().isBlank() || userInfo.getUsername().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<User> usernameOptional = this.userRepo.findByUsername(userInfo.getUsername());
			
			if(usernameOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already in use");
			}
			else {
				Optional<User> emailOptional = this.userRepo.findByEmail(userInfo.getEmail());
				if(emailOptional.isPresent()) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
				}
				else {
					User user = new User(userInfo.getUsername(), userInfo.getName(), userInfo.getEmail(), userInfo.getPassword());
					this.userRepo.save(user);
					return ResponseEntity.status(HttpStatus.CREATED).build();
				}
			}
			
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<Object> login(@RequestBody SimpleUserDTO userInfo){
		if(!userInfo.getName().isEmpty() || userInfo.getPassword().isBlank() || !userInfo.getEmail().isEmpty() || userInfo.getUsername().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<User> usernameOptional = this.userRepo.findByUsername(userInfo.getUsername());
			
			if(!usernameOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password are incorrect");
			}
			else {
				User user = usernameOptional.get();
				if(!user.getPassword().equals(userInfo.getPassword())) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password are incorrect");
				}
				else {
					return ResponseEntity.status(HttpStatus.OK).build();
				}
			}
			
		}
	}
	
	@GetMapping("/users/{username}")
	public ResponseEntity<Object> getUser (@PathVariable String username){
		if(username.isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<User> usernameOptional = this.userRepo.findByUsername(username);
			if(!usernameOptional.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			else {
				UserDTO dto = new UserDTO(usernameOptional.get());
				return ResponseEntity.status(HttpStatus.OK).body(dto);
			}
		}
	}
	
	@PutMapping("/users/{username}")
	public ResponseEntity<Object> updateUser (@PathVariable String username, @RequestBody SimpleUserDTO userInfo){
		if(username.isBlank() || userInfo.getName().isBlank() || userInfo.getPassword().isBlank() || userInfo.getEmail().isBlank() || !userInfo.getUsername().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<User> usernameOptional = this.userRepo.findByUsername(username);
			if(!usernameOptional.isPresent()) {
				return ResponseEntity.notFound().build();
			}
			else {
				User user = usernameOptional.get();
				if(!user.getEmail().equals(userInfo.getEmail())) {
					Optional<User> emailUserInfoOptional = this.userRepo.findByEmail(userInfo.getEmail());
					if(emailUserInfoOptional.isPresent()) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
					}
					else {
						user.setEmail(userInfo.getEmail());
					}
				}
				user.setName(userInfo.getName());
				user.setPassword(userInfo.getPassword());
				this.userRepo.save(user);
				return ResponseEntity.status(HttpStatus.OK).build();
			}
		}
	}

}
