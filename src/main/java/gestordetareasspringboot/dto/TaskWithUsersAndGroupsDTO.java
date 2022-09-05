package gestordetareasspringboot.dto;

import java.util.List;

public class TaskWithUsersAndGroupsDTO {
	private String name;
	private String description;
	private Long group;
	private List<String> users;
	
	public TaskWithUsersAndGroupsDTO() {}
	
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
	public Long getGroup() {
		return group;
	}
	public void setGroup(Long group) {
		this.group = group;
	}
	public List<String> getUsers() {
		return users;
	}
	public void setUsers(List<String> users) {
		this.users = users;
	}
}
