package EventManager.EventManager.event;

import EventManager.EventManager.jpa.EventRepository;
import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import EventManager.EventManager.user.UserJpaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventJpaService {

    private final UserJpaService userJpaService;

    private final EventRepository eventRepository;

    public EventJpaService(UserJpaService userJpaService, EventRepository postRepository) {
        this.userJpaService = userJpaService;
        this.eventRepository = postRepository;
    }

    public Event createEventForUser(long id, Event event){
        var user=userJpaService.findUser(id);
        Event saveEvent = eventRepository.save(event);
        handleUserEvent(user,saveEvent);
        return saveEvent;
    }

    public List<Event> retrieveEventForUser(long userId){
        return getEvents(userJpaService.findUser(userId).getEventsIds());
    }

    public List<Event> retrieveEventForUserByLocation(long userId,String location){
        return getEventsForUserByLocation(userJpaService.findUser(userId).getEventsIds(),location).stream().filter(event -> event.getLocation().equals(location)).toList();
    }

    public List<Event> retrieveEventForUserSortByDate(long userId){
        return getEventsForUserSortEventDate(userJpaService.findUser(userId).getEventsIds());
    }

    public List<Event> retrieveEventForUserSortByCreationTime(long userId){
        return getEventsForUserSortCreationTime(userJpaService.findUser(userId).getEventsIds());
    }

    public List<Event> retrieveEventForUserSortByPopularity(long userId){
        return getEventsForUserSortPopulationDate(userJpaService.findUser(userId).getEventsIds());
    }

    public List<Event> retrieveAllEvents(){
        return eventRepository.findAll();
    }

    public void deleteEvent(long eventId){
        eventRepository.deleteById(eventId);
    }

    public Optional<Event> findEvent(long eventId){return eventRepository.findById(eventId);}

    public void updateEvent(Event updatedeEvent){
        eventRepository.updateEvent(updatedeEvent.getId(),updatedeEvent.getDescription(),updatedeEvent.getPopularity(), updatedeEvent.getLocation(), updatedeEvent.getEventDate());
    }
    private List<Event> getEvents(List<Long> eventIds){
        return eventRepository.findAllById(eventIds);
    }

    private List<Event> getEventsForUserByLocation(List<Long> eventsIds,String location){
        return eventRepository.getEventsForUserByLocation(eventsIds,location);
    }
    private List<Event> getEventsForUserSortEventDate(List<Long> eventsIds){
        return eventRepository.getEventsForUserSortEventDate(eventsIds);
    }
    private List<Event> getEventsForUserSortPopulationDate(List<Long> eventsIds){
        return eventRepository.getEventsForUserSortPopulationDate(eventsIds);
    }
    private List<Event> getEventsForUserSortCreationTime(List<Long> eventsIds){
        return eventRepository.getEventsForUserSortCreationTime(eventsIds);
    }

    private void handleUserEvent(User user,Event event){
        user.addEvent(event);
        userJpaService.updateUserEvents(user.getId(),user.getEventsIds());
    }
}
