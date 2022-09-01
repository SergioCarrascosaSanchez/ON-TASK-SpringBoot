package gestordetareasspringboot.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;

import gestordetareasspringboot.dto.GroupDTO;
import gestordetareasspringboot.dto.GroupListOfTasksDTO;
import gestordetareasspringboot.dto.IdDTO;
import gestordetareasspringboot.dto.ListOfGroupsDTO;
import gestordetareasspringboot.dto.ListOfTasksDTO;
import gestordetareasspringboot.dto.SimpleGroupDTO;
import gestordetareasspringboot.dto.SimpleUserDTO;
import gestordetareasspringboot.dto.TaskDTO;
import gestordetareasspringboot.dto.TaskInfoDTO;
import gestordetareasspringboot.dto.TaskWithUsersAndGroupsDTO;
import gestordetareasspringboot.dto.TokenResponseDTO;
import gestordetareasspringboot.dto.UserDTO;
import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.group.GroupRepository;
import gestordetareasspringboot.jwt.JwtService;
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
	@Autowired
	private JwtService jwtService;
	
	
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
					return ResponseEntity.status(HttpStatus.OK).body(new TokenResponseDTO(this.jwtService.createToken(user.getUsername())));
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
	public ResponseEntity<Object> newTask(@RequestBody TaskWithUsersAndGroupsDTO task){
		if(task.getName().isBlank()||task.getDescription().isBlank() || task.getUsers().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<Group> groupOptional = this.groupRepo.findById(task.getGroup());
			if(groupOptional.isPresent()) {
				LinkedList<User> users = new LinkedList<>();
				for(String user: task.getUsers()) {
					Optional<User> userOptional = this.userRepo.findByUsername(user);
					if(userOptional.isPresent()) {
						users.add(userOptional.get());
					}
					else {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
					}
				}
				Task newTask = new Task(task.getName(), task.getDescription());
				for(User u: users) {
					try {
						newTask.addUser(u);
					} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
					}
					
				}
				Group group = groupOptional.get();
				group.addTask(newTask);
				this.taskRepo.save(newTask);
				this.groupRepo.save(group);
				IdDTO dto = new IdDTO(this.taskRepo.save(newTask).getId());
				return ResponseEntity.status(HttpStatus.CREATED).body(dto);
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
			}
			
			
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
	
	@DeleteMapping("/tasks/{idTask}/groups/{idGroup}")
	public ResponseEntity<Object> deleteTask (@PathVariable Long idTask, @PathVariable Long idGroup){
		Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
		if(groupOptional.isPresent()) {
			Optional<Task> taskOptional = this.taskRepo.findById(idTask);
			if(taskOptional.isPresent()) {
				Group group = groupOptional.get();
				try {
					group.deleteTask(taskOptional.get());
				} catch (Exception e) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
				}
				this.taskRepo.deleteById(idTask);
				this.groupRepo.save(group);
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
		}
		
		
		
	}
	
	@PostMapping("/groups")
	public ResponseEntity<Object> newGroup(@RequestBody SimpleGroupDTO groupName){
		if(groupName.getName().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<Group> nameOptional = this.groupRepo.findByName(groupName.getName());
			if(nameOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else {
				Group group = new Group(groupName.getName());
				IdDTO dto = new IdDTO(this.groupRepo.save(group).getId());
				return ResponseEntity.status(HttpStatus.CREATED).body(dto);
			}
		}
	}
	
	@PutMapping("/groups/{idGroup}")
	public ResponseEntity<Object> updateGroup(@PathVariable Long idGroup, @RequestBody SimpleGroupDTO groupName){
		if(groupName.getName().isBlank()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		else {
			Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
			if(groupOptional.isPresent()) {
				Group g = groupOptional.get();
				g.setName(groupName.getName());
				return ResponseEntity.status(HttpStatus.OK).build();
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			}
		}
	}
	
	@GetMapping("/groups/{idGroup}")
	public ResponseEntity<Object> getGroup(@PathVariable Long idGroup){
		Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
		if(groupOptional.isPresent()) {
			Group group = groupOptional.get();
			GroupDTO dto = new GroupDTO(group);
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}
	
	@PutMapping("/users/{username}/groups/{idGroup}/tasks/{idTask}")
	public ResponseEntity<Object> addTaskToUser (@PathVariable String username, @PathVariable Long idGroup, @PathVariable Long idTask){
		Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
		if(groupOptional.isPresent()) {
			Optional<Task> taskOptional = this.taskRepo.findById(idTask);
			if(taskOptional.isPresent()) {
				Optional<User> userOptional = this.userRepo.findByUsername(username);
				if(userOptional.isPresent()) {
					Group group = groupOptional.get();
					Task task = taskOptional.get();
					User user = userOptional.get();
					try {
						task.addUser(user);
					} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
					}
					group.addTask(task);
					this.taskRepo.save(task);
					this.groupRepo.save(group);
					return ResponseEntity.status(HttpStatus.OK).build();
				}
				else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
				}
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
		}
	}
	
	@GetMapping("/users/{username}/groups/{idGroup}")
	public ResponseEntity<Object> getTaskOfUserOfGroup (@PathVariable String username, @PathVariable Long idGroup){
		Optional<User> userOptional = this.userRepo.findByUsername(username);
		if(userOptional.isPresent()) {
			Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
			if(!groupOptional.isPresent()) {
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
			}
			else {
				User user = userOptional.get();
				Group group = groupOptional.get();
				ListOfTasksDTO dto = new ListOfTasksDTO();
				for(Task t: group.getUserTasks(user)) {
					TaskInfoDTO taskDTO = new TaskInfoDTO(t);
					dto.addTask(taskDTO);
				}
				return ResponseEntity.status(HttpStatus.OK).body(dto);
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		return null; //me da un error extraño y solo se soluciona poniendo eso
	}
	
	@PostMapping("/tasksOfuser/{username}")
	public ResponseEntity<Object> getTaskOfUser (@PathVariable String username, @RequestBody ListOfGroupsDTO groupList){
		Optional<User> userOptional = this.userRepo.findByUsername(username);
		if(userOptional.isPresent()) {
			User user = userOptional.get();
			GroupListOfTasksDTO dto = new GroupListOfTasksDTO();
			for(Long idGroup: groupList.getGroups()) {
				Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
				if(!groupOptional.isPresent()) {
					ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
				}
				else {
					Group group = groupOptional.get();
					List<TaskInfoDTO> lista= new LinkedList<TaskInfoDTO>();
					for(Task t: group.getUserTasks(user)) {
						TaskInfoDTO taskDTO = new TaskInfoDTO(t);
						lista.add(taskDTO);
					}
					dto.addEntry(idGroup, group.getName(), lista);
					
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	}
	
	@PutMapping("/users/{username}/groups/{idGroup}")
	public ResponseEntity<Object> addUserToGroup (@PathVariable String username, @PathVariable Long idGroup, @RequestParam String type){
		Optional<User> userOptional = this.userRepo.findByUsername(username);
		if(userOptional.isPresent()) {
			Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
			if(groupOptional.isPresent()) {
				Group group = groupOptional.get();
				User user = userOptional.get();
				if(type.equals("delete")) {
					try {
						group.deleteUser(user);
					} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
					}
					this.groupRepo.save(group);
					this.userRepo.save(user);
					return ResponseEntity.status(HttpStatus.OK).build();
				}
				else {
					try {
						group.addUser(user);
					} catch (Exception e) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
					}
					this.groupRepo.save(group);
					return ResponseEntity.status(HttpStatus.OK).build();
				}
				
				
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	}
}






