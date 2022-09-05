package gestordetareasspringboot.rest;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<Object> getUser (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader ,@PathVariable String username){
		if(validHeaderToken(authHeader, username, false)) {
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PutMapping("/users/{username}")
	public ResponseEntity<Object> updateUser (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable String username, @RequestBody SimpleUserDTO userInfo){
		if(validHeaderToken(authHeader, username, true)) {
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
		}else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PostMapping("/tasks")
	public ResponseEntity<Object> newTask(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody TaskWithUsersAndGroupsDTO task){
		if(validHeaderToken(authHeader, "", false)) {
			if(task.getName().isBlank()||task.getDescription().isBlank() || task.getUsers().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else {
				Optional<Group> groupOptional = this.groupRepo.findById(task.getGroup());
				if(groupOptional.isPresent()) {
					Group group = groupOptional.get();
					if(isInGroup(group, this.jwtService.getSubject(this.jwtService.getToken(authHeader)))){
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
						group.addTask(newTask);
						this.taskRepo.save(newTask);
						this.groupRepo.save(group);
						IdDTO dto = new IdDTO(this.taskRepo.save(newTask).getId());
						return ResponseEntity.status(HttpStatus.CREATED).body(dto);
					}
					else {
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					}
				}
				else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
				}
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
	}
	
	@GetMapping("/tasks/{id}")
	public ResponseEntity<Object> getTask (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id, @RequestParam String type){
		if(validHeaderToken(authHeader, "", false)) {
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	@PutMapping("/tasks/{id}")
	public ResponseEntity<Object> updateTask (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long id, @RequestBody TaskInfoDTO taskInfo){
		if(validHeaderToken(authHeader, "", false)) {
			if(taskInfo.getName().isBlank() || taskInfo.getDescription().isBlank()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else {
				Optional<Task> optionalTask = this.taskRepo.findById(id);
				if(optionalTask.isPresent()) {
					Task task = optionalTask.get();
					boolean tokenUsernameInTask = false;
					for(User taskUser: task.getUsers()) {
						if(taskUser.getUsername().equals(this.jwtService.getSubject(this.jwtService.getToken(authHeader)))) {
							tokenUsernameInTask = true;
						}
					}
					if(tokenUsernameInTask) {
						task.setName(taskInfo.getName());
						task.setDescription(taskInfo.getDescription());
						this.taskRepo.save(task);
						return ResponseEntity.status(HttpStatus.OK).build();
					}
					else {
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					}
				}
				else {
					return ResponseEntity.notFound().build();
				}
			}	
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@DeleteMapping("/tasks/{idTask}/groups/{idGroup}")
	public ResponseEntity<Object> deleteTask (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long idTask, @PathVariable Long idGroup){
		if(validHeaderToken(authHeader, "", false)) {
			Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
			if(groupOptional.isPresent()) {
				Group group = groupOptional.get();
				if(isInGroup(group, this.jwtService.getSubject(this.jwtService.getToken(authHeader)))){
					Optional<Task> taskOptional = this.taskRepo.findById(idTask);
					if(taskOptional.isPresent()) {
						
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
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		
		
	}
	
	@PostMapping("/groups")
	public ResponseEntity<Object> newGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestBody SimpleGroupDTO groupName){
		if(validHeaderToken(authHeader, "", false)) {
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	
	@PutMapping("/groups/{idGroup}")
	public ResponseEntity<Object> updateGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable Long idGroup, @RequestBody SimpleGroupDTO groupName){
		if(validHeaderToken(authHeader, "", false)) {
			if(groupName.getName().isBlank()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			else {
				Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
				if(groupOptional.isPresent()) {
					Group g = groupOptional.get();
					if(isInGroup(g, this.jwtService.getSubject(this.jwtService.getToken(authHeader)))){
						g.setName(groupName.getName());
						this.groupRepo.save(g);
						return ResponseEntity.status(HttpStatus.OK).build();
					}
					else {
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
					}	
				}
				else {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				}
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	
	@GetMapping("/groups/{idGroup}")
	public ResponseEntity<Object> getGroup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,@PathVariable Long idGroup){
		if(validHeaderToken(authHeader, "", false)) {
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	
	@PutMapping("/users/{username}/groups/{idGroup}/tasks/{idTask}")
	public ResponseEntity<Object> addTaskToUser (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,@PathVariable String username, @PathVariable Long idGroup, @PathVariable Long idTask){
		if(validHeaderToken(authHeader, "", false)) {
			Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
			if(groupOptional.isPresent()) {
				Group group = groupOptional.get();
				if(isInGroup(group, this.jwtService.getSubject(this.jwtService.getToken(authHeader)))){
					if(isInGroup(group,username)){
						Optional<Task> taskOptional = this.taskRepo.findById(idTask);
						if(taskOptional.isPresent()) {
							Optional<User> userOptional = this.userRepo.findByUsername(username);
							if(userOptional.isPresent()) {
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
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not assigned to this group");
					}
				}
				else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}
			}
			else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
			}
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@GetMapping("/users/{username}/groups/{idGroup}")
	public ResponseEntity<Object> getTaskOfUserOfGroup (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,@PathVariable String username, @PathVariable Long idGroup){
		if(validHeaderToken(authHeader, "", false)) {
			Optional<User> userOptional = this.userRepo.findByUsername(username);
			if(userOptional.isPresent()) {
				Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
				if(!groupOptional.isPresent()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Group not found");
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
		}
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PostMapping("/tasksOfuser/{username}")
	public ResponseEntity<Object> getTaskOfUser (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @PathVariable String username, @RequestBody ListOfGroupsDTO groupList){
		if(validHeaderToken(authHeader, "", false)) {
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PutMapping("/users/{username}/groups/{idGroup}")
	public ResponseEntity<Object> addUserToGroup (@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,  @PathVariable String username, @PathVariable Long idGroup, @RequestParam String type){
		if(validHeaderToken(authHeader, "", false)) {
			Optional<User> userOptional = this.userRepo.findByUsername(username);
			if(userOptional.isPresent()) {
				Optional<Group> groupOptional = this.groupRepo.findById(idGroup);
				if(groupOptional.isPresent()) {
					Group group = groupOptional.get();
					if(this.jwtService.getSubject(this.jwtService.getToken(authHeader)).equals(username)){
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
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
		else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	private boolean validHeaderToken(String authHeader, String username, boolean usernameNeeded) {
		if(jwtService.isBearer(authHeader)) {
			String token = jwtService.getToken(authHeader);
			
			if(jwtService.verify(token, username, usernameNeeded)) {
				return true;
			}
			else {
				return false;
			}	
		}
		else {
			return false;
		}
	}
	
	private boolean isInGroup(Group group, String username) {
		for(User groupUser: group.getUsers()) {
			if(groupUser.getUsername().equals(username)) {
				return true;
			}
		}
		return false;
	}
}






