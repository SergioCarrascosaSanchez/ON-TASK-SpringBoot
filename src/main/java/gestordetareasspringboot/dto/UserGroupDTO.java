package gestordetareasspringboot.dto;

import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.user.User;

public class UserGroupDTO {
	private String username;
	private Long group;
	
	public UserGroupDTO() {}
	
	public UserGroupDTO(User u, Group g) {
		this.username = u.getUsername();
		this.group = g.getId();
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getGroup() {
		return group;
	}
	public void setGroup(Long group) {
		this.group = group;
	}
}
