package gestordetareasspringboot.dto;

import java.util.HashMap;
import java.util.Map;

public class GroupListOfTasksDTO {
	private Map<Long, ListOfTasksDTO> tasks;
	
	public GroupListOfTasksDTO() {
		this.tasks = new HashMap<>();
	}
	
	public void addEntry(Long group, ListOfTasksDTO list) {
		this.tasks.put(group, list);
	}
}
