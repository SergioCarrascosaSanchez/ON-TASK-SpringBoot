package gestordetareasspringboot.task;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import gestordetareasspringboot.user.User;

@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	private String description;
	private String datetime;
	@OneToMany
	private Set<User> users;
	
	public Task() {	}
	
	public Task(String name, String descrip) {
		this.name = name;
		this.description = descrip;
		this.datetime = LocalDateTime.now().toString();
		this.users = new HashSet<User>();
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String descr) {
		this.description = descr;
	}
	
	public String getDateTime() {
		return this.datetime.toString();
	}
	
	public Iterable<User> getUsers(){
		return this.users;
	}
	
	public void addUser(User user) throws Exception {
		if(this.users.contains(user)) {
			throw new Exception("User already assigned to this task");
		}
		else {
			this.users.add(user);
		}
	}
	
	public void deleteUser(User user) throws Exception {
		if(!this.users.contains(user)) {
			throw new Exception("User not assigned to this task");
		}
		else {
			this.users.remove(user);
		}
	}
	
	
}
