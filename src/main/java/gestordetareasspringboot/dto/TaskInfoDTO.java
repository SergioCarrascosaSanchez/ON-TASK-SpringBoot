package gestordetareasspringboot.dto;

import gestordetareasspringboot.task.Task;

public class TaskInfoDTO {
	private String name;
	private String descripcion;
	
	public TaskInfoDTO() {}
	
	public TaskInfoDTO(Task task) {
		this.name = task.getName();
		this.descripcion = task.getDescription();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
