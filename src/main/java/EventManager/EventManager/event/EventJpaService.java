package EventManager.EventManager.event;

import EventManager.EventManager.jpa.EventRepository;
import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.user.UserJpaService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class EventJpaService {

    private final UserJpaService userJpaService;

    private final EventRepository eventRepository;

    public EventJpaService(UserJpaService userJpaService, EventRepository postRepository) {
        this.userJpaService = userJpaService;
        this.eventRepository = postRepository;
    }

    public Event createPostForUser(long id, Event event){
        var user=userJpaService.findUser(id);
        user.addEvent(event);
        return eventRepository.save(event);
    }

    public List<Event> retrieveEventForUser(long id){
        return userJpaService.findUser(id).getEvents();
    }

    public List<Event> retrieveEventForUserByLocation(long id,String location){
        return userJpaService.findUser(id).getEvents().stream().filter(event -> event.getLocation().equals(location)).toList();
    }

    public List<Event> retrieveEventForUserSortByDate(long id){
        return userJpaService.findUser(id).getEvents().stream().sorted(Event.compareEventsByDate).toList();
    }

    public List<Event> retrieveEventForUserSortByCreationTime(long id){
        return userJpaService.findUser(id).getEvents().stream().sorted(Event.compareEventsByCreationTime).toList();
    }

    public List<Event> retrieveEventForUserSortByPopularity(long id){
        return userJpaService.findUser(id).getEvents().stream().sorted(Event.compareEventsByPopularity).toList();
    }

    public List<Event> retrieveAllPosts(){
        return eventRepository.findAll();
    }

    public void deleteEvent(long id){
        eventRepository.deleteById(id);
    }

    public void updateEvent(Event updatedeEvent){
        eventRepository.updateEvent(updatedeEvent.getId(),updatedeEvent.getDescription(),updatedeEvent.getPopularity());
    }
}
