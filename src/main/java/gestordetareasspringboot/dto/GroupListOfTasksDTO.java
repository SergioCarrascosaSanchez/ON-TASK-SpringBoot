package gestordetareasspringboot.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupListOfTasksDTO {
	private Map<Long, String> names;
	private Map<Long, List<TaskInfoDTO>> tasks;
	
	public GroupListOfTasksDTO() {
		this.tasks = new HashMap<>();
		this.names = new HashMap<>();
	}
	
	public void addEntry(Long group, String name, List<TaskInfoDTO> list) {
		this.tasks.put(group, list);
		this.names.put(group, name);
	}
	
	public Map<Long, List<TaskInfoDTO>> getTasks(){
		return this.tasks;
	}
	
	public Map<Long, String> getNames(){
		return this.names;
	}
}
