package EventManager.EventManager.jpa;

import EventManager.EventManager.jpa.beans.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    @Transactional
    @Modifying
    @Query("update User as u set u.events = :eventIds where u.id = :userId")
    void updateUserEvent(long userId,List<Long> eventIds);
}
