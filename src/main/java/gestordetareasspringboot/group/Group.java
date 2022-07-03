package gestordetareasspringboot.group;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

@Entity
public class Group {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	@ManyToMany(mappedBy="groups")
	private Set<User> users;
	@OneToMany
	private Set<Task> tasks;
	
	public Group() {}
	
	public Group(String name) {
		this.name = name;
		this.users = new HashSet<>();
		this.tasks = new HashSet<>();
	}
	
	public Long getId() {
		return this.id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addUser(User user) throws Exception {
		if(this.users.contains(user)) {
			throw new Exception("User already assigned to this group");
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
	public void addTask(Task task) throws Exception {
		if(this.tasks.contains(task)) {
			throw new Exception("Task already assigned to this group");
		}
		else {
			this.tasks.add(task);
		}
	}
	public void deleteTask(Task task) throws Exception {
		if(!this.tasks.contains(task)) {
			throw new Exception("Task not assigned to this group");
		}
		else {
			this.tasks.remove(task);
		}
	}
	
	public Iterable<Task> getUserTasks(User user){
		HashSet<Task> taskSet = new HashSet<>();
		for(Task t: this.tasks) {
			for(User u :t.getUsers()) {
				if(u.equals(user)) {
					taskSet.add(t);
					break;
				}
			}
		}
		return taskSet;
	}
	
	public Iterable<User> getUsers(){
		return this.users;
	}
	
	public Iterable<Task> getTasks(){
		return this.tasks;
	}
	
	

}
