package gestordetareasspringboot.dto;

public class ErrorDTO {
	private String error;
	
	public ErrorDTO() {}
	
	public ErrorDTO(String descripcion) {
		this.setError(descripcion);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}
