package gestordetareasspringboot.dto;

import gestordetareasspringboot.user.User;

public class SimpleUserDTO {
	private String username;
	private String name;
	private String email;
	private String password;
	
	public SimpleUserDTO() {}
	
	public SimpleUserDTO(User u) {
		this.username = u.getUsername();
		this.name = u.getName();
		this.email = u.getEmail();
		this.password = u.getPassword();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	
}
