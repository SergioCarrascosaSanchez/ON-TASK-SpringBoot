package gestordetareasspringboot.dto;

import gestordetareasspringboot.task.Task;

public class TaskInfoDTO {
	private Long id;
	private String name;
	private String description;
	
	public TaskInfoDTO() {}
	
	public TaskInfoDTO(Task task) {
		this.setId(task.getId());
		this.name = task.getName();
		this.description = task.getDescription();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String descripcion) {
		this.description = descripcion;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
