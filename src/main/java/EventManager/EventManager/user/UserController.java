package EventManager.EventManager.user;

import EventManager.EventManager.event.EventJpaService;
import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import EventManager.EventManager.remainder.EventRemainderService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserJpaService userJpaService;
    private final EventJpaService eventJpaService;

    private final EventRemainderService eventRemainderService;

    public UserController(UserJpaService userJpaService, EventJpaService eventJpaService, EventRemainderService eventRemainderService) {
        this.userJpaService = userJpaService;
        this.eventJpaService = eventJpaService;
        this.eventRemainderService = eventRemainderService;
    }

    @GetMapping(path = "/users/{id}")
    public EntityModel<User> findUserById(@PathVariable int id){
        return EntityModel.of(userJpaService.findUser(id));
    }

    @PostMapping(path = "/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        System.out.println("user "+ user);
       return ResponseEntity.of(Optional.of(userJpaService.createUser(user)));

    }

    @DeleteMapping(path = "/users/{id}")
    public void deleteUser(@PathVariable long id){
        userJpaService.deleteUser(id);
    }

    @GetMapping(path = "/users/{id}/events")
    public ResponseEntity<List<Event>> getEventsForUser(@PathVariable long id){
        return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUser(id)));
    }

    @GetMapping(path = "/users/{id}/events/sortByCreationTime")
    public ResponseEntity<List<Event>> getEventsSortByCreationTimeForUser(@PathVariable long id){
        return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUserSortByCreationTime(id)));
    }

    @GetMapping(path = "/users/{id}/sortByDate")
    public ResponseEntity<List<Event>> getEventsSortByDateForUser(@PathVariable long id){
        return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUserSortByDate(id)));
    }

    @GetMapping(path = "/users/{id}/events")
    public ResponseEntity<List<Event>> getEventsSortByPopularityForUser(@PathVariable long id){
        return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUserSortByPopularity(id)));
    }

    @GetMapping(path = "/users/{id}/events/{location}")
    public ResponseEntity<List<Event>> getEventsForUserByLocation(@PathVariable long id,@PathVariable String location){
        return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUserByLocation(id,location)));
    }

    @PostMapping(path = "/users/{id}/event")
    public ResponseEntity<Event> createEvent(@PathVariable long id, @RequestBody @Valid Event event){
        Event savedEvent=eventJpaService.createPostForUser(id,event);
        eventRemainderService.runRemainder(id,event);
        return ResponseEntity.of(Optional.of(savedEvent));
    }

    @PostMapping(path = "/users/{id}/deleteEvent")
    public void deleteEvent(@RequestBody @Valid Event event){
       eventJpaService.deleteEvent(event.getId());
       eventRemainderService.deleteRemainder(event);
    }

    @PostMapping(path = "/users/{id}/updateEvent")
    public void updateEvent(@PathVariable long id,@RequestBody @Valid Event event){
        eventJpaService.updateEvent(event);
        eventRemainderService.updateRemainder(id,event);
    }




}
