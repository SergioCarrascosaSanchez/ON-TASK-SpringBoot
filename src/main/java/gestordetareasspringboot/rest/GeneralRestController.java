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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gestordetareasspringboot.dto.IdDTO;
import gestordetareasspringboot.dto.SimpleUserDTO;
import gestordetareasspringboot.dto.TaskDTO;
import gestordetareasspringboot.dto.TaskInfoDTO;
import gestordetareasspringboot.dto.UserDTO;
import gestordetareasspringboot.group.GroupRepository;
import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.task.TaskRepository;
import gestordetareasspringboot.user.User;
import gestordetareasspringboot.user.UserRepository;

@RestController
public class GeneralRestController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private TaskRepository taskRepo;
	@Autowired
	private GroupRepository groupRepo;
	
	
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
	
	@PostMapping("/tasks")
	public ResponseEntity<Object> newTask(@RequestBody TaskInfoDTO task){
		if(task.getName().isBlank()||task.getDescription().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Task newTask = new Task(task.getName(), task.getDescription());
			this.taskRepo.save(newTask);
			IdDTO dto = new IdDTO(this.taskRepo.save(newTask).getId());
			return ResponseEntity.status(HttpStatus.CREATED).body(dto);
		}
	}
	@GetMapping("/tasks/{id}")
	public ResponseEntity<Object> getTask (@PathVariable Long id, @RequestParam String type){
		Optional<Task> optionalTask = this.taskRepo.findById(id);
		if(optionalTask.isPresent()) {
			Task task = optionalTask.get();
			if(type.equals("simple")) {
				TaskInfoDTO dto = new TaskInfoDTO(task);
				return ResponseEntity.status(HttpStatus.OK).body(dto);
			}
			else if(type.equals("complete")) {
				TaskDTO dto = new TaskDTO(task);
				return ResponseEntity.status(HttpStatus.OK).body(dto);
			}
			else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	@PutMapping("/tasks/{id}")
	public ResponseEntity<Object> updateTask (@PathVariable Long id, @RequestBody TaskInfoDTO taskInfo){
		if(taskInfo.getName().isBlank() || taskInfo.getDescription().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<Task> optionalTask = this.taskRepo.findById(id);
			if(optionalTask.isPresent()) {
				Task task = optionalTask.get();
				task.setName(taskInfo.getName());
				task.setDescription(taskInfo.getDescription());
				this.taskRepo.save(task);
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			else {
				return ResponseEntity.notFound().build();
			}
		}
	}
}
