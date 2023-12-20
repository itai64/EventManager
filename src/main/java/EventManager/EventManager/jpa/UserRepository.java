package EventManager.EventManager.jpa;

import EventManager.EventManager.jpa.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
