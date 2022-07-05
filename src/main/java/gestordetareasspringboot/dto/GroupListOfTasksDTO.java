package gestordetareasspringboot.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListOfTasksDTO {
	private Map<Long, List<TaskInfoDTO>> tasks;
	
	public GroupListOfTasksDTO() {
		this.tasks = new HashMap<>();
	}
	
	public void addEntry(Long group, List<TaskInfoDTO> list) {
		this.tasks.put(group, list);
	}
	
	public Map<Long, List<TaskInfoDTO>> getTasks(){
		return this.tasks;
	}
}
