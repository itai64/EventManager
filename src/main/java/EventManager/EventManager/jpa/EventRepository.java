package EventManager.EventManager.jpa;

import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {

    @Transactional
    @Modifying
    @Query("update Event as e set description = :description, popularity = :popularity, e.location = :location , e.eventDate = :eventDate where e.id = :id")
    void updateEvent(long id, String description, long popularity, String location, LocalDateTime eventDate);

    @Query("select e from Event as e where e.id in :eventIds order by e.creationTime")
    List<Event> getEventsForUserSortCreationTime(List<Long> eventIds);

    @Query("select e from Event as e where e.id in :eventIds order by e.eventDate")
    List<Event> getEventsForUserSortEventDate(List<Long> eventIds);

    @Query("select e from Event as e where e.id in :eventIds order by e.popularity")
    List<Event> getEventsForUserSortPopulationDate(List<Long> eventIds);

    @Query("select e from Event as e where e.id in :eventIds and e.location = :location")
    List<Event> getEventsForUserByLocation(List<Long> eventIds,String location);
}
