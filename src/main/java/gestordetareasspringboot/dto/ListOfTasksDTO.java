package gestordetareasspringboot.dto;

import java.util.LinkedList;
import java.util.List;

public class ListOfTasksDTO {
	private List<TaskInfoDTO> tasks;
	
	public ListOfTasksDTO() {
		this.tasks = new LinkedList<>();
	}
	
	public void addTask(TaskInfoDTO task) {
		this.tasks.add(task);
	}
	
	public List<TaskInfoDTO> getTasks(){
		return this.tasks;
	}
	public void setTasks(List<TaskInfoDTO> list) {
		this.tasks = list;
	}
}
