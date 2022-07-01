package gestordetareasspringboot.user;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import gestordetareasspringboot.group.Group;

@Entity
public class User {
	
	@Id
	private String username;
	private String name;
	private String email;
	private String password;
	@ManyToMany
	private Set<Group> groups;
	
	public User() {}
	
	public User(String username, String name, String email, String password) {
		this.username = username;
		this.name = name;
		this.email = email;
		this.password = password;
		this.groups = new HashSet<>();
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUsername() {
		return this.username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Iterable<Groups> getGroups(){
		return this.groups;
	}
	
	public void addToGroup(Group group) throws Exception {
		if(this.groups.contains(group)) {
			throw new Exception("User already assigned to this group");
		}
		else {
			this.groups.add(group);
		}
	}
	
	public void deleteGroup(Group group) throws Exception {
		if(!this.groups.contains(group)) {
			throw new Exception("User not assigned to this group");
		}
		else {
			this.groups.remove(group);
		}
	}

}
