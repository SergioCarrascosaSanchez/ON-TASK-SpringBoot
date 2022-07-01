package gestordetareasspringboot.dto;

import java.util.LinkedList;
import java.util.List;

import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

public class GroupDTO {
	private Long id;
	private String name;
	private List<String> users;
	private List<Long> tasks;
	
	public GroupDTO(){}
	
	public GroupDTO(Group g){
		this.id = g.getId();
		this.name = g.getName();
		List<String> usernames = new LinkedList<>();
		for(User u: g.getUsers()) {
			usernames.add(u.getUsername());
		}
		this.users = usernames;
		List<Long> tasksId = new LinkedList<>();
		for(Task t: g.getTasks()) {
			tasksId.add(t.getId());
		}
		this.tasks = tasksId;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
	public List<Long> getTasks() {
		return tasks;
	}
	public void setTasks(List<Long> tasks) {
		this.tasks = tasks;
	}
}
