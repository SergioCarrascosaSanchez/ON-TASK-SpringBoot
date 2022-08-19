package gestordetareasspringboot.dto;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;

import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

public class TaskDTO {
	private Long id;
	private String name;
	private String description;
	private String datetime;
	private List<SimpleUserDTO> users;
	
	public TaskDTO() {}
	
	public TaskDTO(Task task) {
		this.name = task.getName();
		this.id = task.getId();
		this.description = task.getDescription();
		this.datetime = task.getDateTime();
		this.users = new LinkedList<SimpleUserDTO>();
		for(User u: task.getUsers()) {
			SimpleUserDTO userInfo = new SimpleUserDTO(u);
			this.users.add(userInfo);
		}
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
	public List<SimpleUserDTO> getUsers() {
		return users;
	}
	public void setUsers(List<SimpleUserDTO> users) {
		this.users = users;
	}
}
