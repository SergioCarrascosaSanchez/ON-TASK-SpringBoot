package gestordetareasspringboot.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gestordetareasspringboot.group.Group;
import gestordetareasspringboot.task.Task;
import gestordetareasspringboot.user.User;

public class UserDTO {
	private String username;
	private String name;
	private List<Long> groups;
	//private String mapUserTasks;
	
	public UserDTO() {}
	
	public UserDTO(User user) {
		this.username = user.getUsername();
		this.name = user.getName();
		
		/*Alternativa, aunque requerirá bastantes API calls*/
		 List<Long> l = new LinkedList<>();
		 for(Group g: user.getGroups()) {
		 	l.add(g.getId());
		 }
		 this.groups = l;
		 
		 
		/*
		 
		Esta seria la opcion de enviar TODO en una misma llamada, pero no se si funcionaria
		
		 
		Map<Long,Map<Long, ArrayList<String>>> map = new HashMap<>();
		for(Group g: user.getGroups()) {
			Long groupId = g.getId();
			Map<Long, ArrayList<String>> nestedMap = new HashMap<>();
			for(Task t: g.getUserTasks(user)) {
				ArrayList<String> arr = new ArrayList<>(2);
				arr.add(0, t.getName());
				arr.add(1, t.getDescription());
				nestedMap.put(t.getId(), arr);
			}
			map.put(groupId, nestedMap);
		}
		try {
			this.mapUserTasks = new ObjectMapper().writeValueAsString(map);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}*/
		
		
		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Long> getGroups() {
		return groups;
	}
	
}
