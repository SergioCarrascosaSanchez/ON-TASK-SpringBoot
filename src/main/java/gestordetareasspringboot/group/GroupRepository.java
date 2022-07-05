package gestordetareasspringboot.group;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gestordetareasspringboot.user.User;

public interface GroupRepository extends JpaRepository<Group, Long>{
	public Optional<Group> findByName(String name);
}
