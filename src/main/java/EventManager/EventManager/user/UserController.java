package EventManager.EventManager.user;

import EventManager.EventManager.event.EventJpaService;
import EventManager.EventManager.event.beans.EventByLocationResults;
import EventManager.EventManager.event.beans.EventsSortByCreationDateResults;
import EventManager.EventManager.event.beans.EventsSortByEventDateResults;
import EventManager.EventManager.event.beans.EventsSortByPopularityResults;
import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;
import EventManager.EventManager.remainder.EventRemainderService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserJpaService userJpaService;
    private final EventJpaService eventJpaService;
    private final EventRemainderService eventRemainderService;
    private final Bucket bucket;

    public UserController(UserJpaService userJpaService, EventJpaService eventJpaService, EventRemainderService eventRemainderService) {
        this.userJpaService = userJpaService;
        this.eventJpaService = eventJpaService;
        this.eventRemainderService = eventRemainderService;
        Bandwidth limit = Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @GetMapping(path = "/users/{id}")
    public EntityModel<User> findUserById(@PathVariable int id){
        return EntityModel.of(userJpaService.findUser(id));
    }

    @PostMapping(path = "/users/createUser")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(userJpaService.createUser(user)));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(path = "/users/{id}/deleteUser")
    public ResponseEntity<User> deleteUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                userJpaService.deleteUser(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/getUserEvents")
    public ResponseEntity<List<Event>> getEventsForUser(@PathVariable long id){

        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(eventJpaService.retrieveEventForUser(id)));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/events/sortByCreationTime")
    public ResponseEntity<EventsSortByCreationDateResults> getEventsSortByCreationTimeForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsSortByCreationDateResults(eventJpaService.retrieveEventForUserSortByCreationTime(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/events/sortByDate")
    public ResponseEntity<EventsSortByEventDateResults> getEventsSortByEventDateForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsSortByEventDateResults(eventJpaService.retrieveEventForUserSortByDate(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping(path = "/users/{id}/events/sortByPopularity")
    public ResponseEntity<EventsSortByPopularityResults> getEventsSortByPopularityForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsSortByPopularityResults(eventJpaService.retrieveEventForUserSortByPopularity(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/events/locationFilter/{location}")
    public ResponseEntity<EventByLocationResults> getEventsByLocationForUser(@PathVariable long id, @PathVariable String location){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventByLocationResults(eventJpaService.retrieveEventForUserByLocation(id, location))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{id}/createEvent")
    public ResponseEntity<Event> createEvent(@PathVariable long id,@Valid @RequestBody Event event){
        try {
            if (bucket.tryConsume(1)) {
                Event savedEvent = eventJpaService.createEventForUser(id, event);
                eventRemainderService.runRemainder(id, savedEvent);
                return ResponseEntity.of(Optional.of(savedEvent));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        catch (Exception e){
           e.printStackTrace();
           return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/getEventById/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable long id,@PathVariable long eventId){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(eventJpaService.findEvent(eventId));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

    }

    @DeleteMapping(path = "/users/{id}/deleteEvent")
    public ResponseEntity<User> deleteEvent(@RequestBody @Valid Event event){
        try {
        if (bucket.tryConsume(1)) {
            eventJpaService.deleteEvent(event.getId());
            eventRemainderService.deleteRemainder(event);
            return ResponseEntity.ok().build();
        }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{id}/updateEvent")
    public ResponseEntity<Event> updateEvent(@PathVariable long id,@RequestBody @Valid Event event){
        try {
            if (bucket.tryConsume(1)) {
                eventJpaService.updateEvent(event);
                eventRemainderService.updateRemainder(id, event);
                return ResponseEntity.of(Optional.of(event));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
    }
    }
}
