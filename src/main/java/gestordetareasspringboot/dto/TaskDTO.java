package gestordetareasspringboot.dto;

import java.util.LinkedList;
import java.util.List;

import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

public class TaskDTO {
	private Long id;
	private String name;
	private String description;
	private String datetime;
	private List<String> users;
	
	public TaskDTO() {}
	
	public TaskDTO(Task task) {
		this.name = task.getName();
		this.id = task.getId();
		this.description = task.getDescription();
		this.datetime = task.getDateTime();
		List<String> usernames = new LinkedList<>();
		for(User u: task.getUsers()) {
			usernames.add(u.getUsername());
		}
		this.users = usernames;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDatetime() {
		return datetime;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
}
