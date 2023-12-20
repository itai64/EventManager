package EventManager.EventManager.jpa;

import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EventRepository extends JpaRepository<Event,Long> {
    @Query("update events set description = :description, popularity = :popularity where events.id = :id")
    void updateEvent(long id, String description, long popularity);
}
