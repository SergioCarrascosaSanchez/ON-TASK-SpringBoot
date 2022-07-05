package gestordetareasspringboot.dto;

import java.util.List;

import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.user.User;

public class ListOfGroupsDTO {
	private List<Long> groups;
	
	public ListOfGroupsDTO() {}
	
	public List<Long> getGroups() {
		return groups;
	}
	public void setGroups(List<Long> list) {
		this.groups = list;
	}
	public void addGroup(Long id) {
		this.groups.add(id);
	}
	
}
