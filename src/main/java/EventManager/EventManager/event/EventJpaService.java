package EventManager.EventManager.event;

import EventManager.EventManager.jpa.EventRepository;
import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import EventManager.EventManager.user.UserJpaService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Event> retrieveEventForUser(long id){
        return getEvents(userJpaService.findUser(id).getEventsIds());
    }

    public List<Event> retrieveEventForUserByLocation(long id,String location){
        return getEventsForUserByLocation(userJpaService.findUser(id).getEventsIds(),location).stream().filter(event -> event.getLocation().equals(location)).toList();
    }

    public List<Event> retrieveEventForUserSortByDate(long id){
        return getEventsForUserSortEventDate(userJpaService.findUser(id).getEventsIds());
    }

    public List<Event> retrieveEventForUserSortByCreationTime(long id){
        return getEventsForUserSortCreationTime(userJpaService.findUser(id).getEventsIds());
    }

    public List<Event> retrieveEventForUserSortByPopularity(long id){
        return getEventsForUserSortPopulationDate(userJpaService.findUser(id).getEventsIds());
    }

    public List<Event> retrieveAllEvents(){
        return eventRepository.findAll();
    }

    public void deleteEvent(long id){
        eventRepository.deleteById(id);
    }

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
        int size = user.getEventsIds().size();
        System.out.println("user events size is: "+size+", current event id is:"+ user.getEventsIds().get(size-1));
        userJpaService.updateUserEvents(user.getId(),user.getEventsIds());
    }
}
