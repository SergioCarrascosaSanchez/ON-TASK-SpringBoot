package gestordetareasspringboot.dto;

import java.util.LinkedList;
import java.util.List;

import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

public class GroupDTO {
	private Long id;
	private String name;
	private List<SimpleUserDTO> users;
	private List<TaskInfoDTO> tasks;
	
	public GroupDTO(){}
	
	public GroupDTO(Group g){
		this.id = g.getId();
		this.name = g.getName();

		this.users = new LinkedList<SimpleUserDTO>();
		for(User u: g.getUsers()) {
			SimpleUserDTO userInfo = new SimpleUserDTO(u);
			this.users.add(userInfo);
		}
		
		this.tasks = new LinkedList<TaskInfoDTO>();
		for(Task t: g.getTasks()) {
			TaskInfoDTO taskInfo = new TaskInfoDTO(t);
			this.tasks.add(taskInfo);
		}
		
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
	public List<SimpleUserDTO> getUsers() {
		return users;
	}
	public void setUsers(List<SimpleUserDTO> users) {
		this.users = users;
	}
	public List<TaskInfoDTO> getTasks() {
		return tasks;
	}
	public void setTasks(List<TaskInfoDTO> tasks) {
		this.tasks = tasks;
	}
}
