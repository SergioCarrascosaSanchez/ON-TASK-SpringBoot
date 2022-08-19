package gestordetareasspringboot.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String>{

	public Optional<User> findByUsername(String username);
	public Optional<User> findByEmail(String email);

}
