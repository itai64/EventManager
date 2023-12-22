package EventManager.EventManager.user;

import EventManager.EventManager.event.EventJpaService;
import EventManager.EventManager.event.beans.*;
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
import java.util.stream.Stream;

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
    public ResponseEntity<EventsResults> getEventsSortByCreationTimeForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsResults(eventJpaService.retrieveEventForUserSortByCreationTime(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/events/sortByDate")
    public ResponseEntity<EventsResults> getEventsSortByEventDateForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsResults(eventJpaService.retrieveEventForUserSortByDate(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    @GetMapping(path = "/users/{id}/events/sortByPopularity")
    public ResponseEntity<EventsResults> getEventsSortByPopularityForUser(@PathVariable long id){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsResults(eventJpaService.retrieveEventForUserSortByPopularity(id))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping(path = "/users/{id}/events/locationFilter/{location}")
    public ResponseEntity<EventsResults> getEventsByLocationForUser(@PathVariable long id, @PathVariable String location){
        try {
            if (bucket.tryConsume(1)) {
                return ResponseEntity.of(Optional.of(new EventsResults(eventJpaService.retrieveEventForUserByLocation(id, location))));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{userId}/createEvent")
    public ResponseEntity<Event> createEvent(@PathVariable long userId,@Valid @RequestBody Event event){
        try {
            if (bucket.tryConsume(1)) {
                Event savedEvent = eventJpaService.createEventForUser(userId, event);
                eventRemainderService.runRemainder(userId, savedEvent);
                return ResponseEntity.of(Optional.of(savedEvent));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        catch (Exception e){
           e.printStackTrace();
           return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{userId}/subscribeEvent")
    public ResponseEntity<Event> subscribeEvent(@PathVariable long userId,@Valid @RequestBody long eventId){
        try {
            if (bucket.tryConsume(1)) {
                Optional<Event> selectedEvent = eventJpaService.findEvent(eventId);
                if (selectedEvent.isEmpty()){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                User user = userJpaService.findUser(userId);
                selectedEvent.get().addListener(user);
                return ResponseEntity.of(selectedEvent);
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{id}/createMultiplyEvent")
    public ResponseEntity<EventsResults> createMultiplyEvent(@PathVariable long id, @Valid @RequestBody List<Event> events){
        try {
            events.forEach(event -> createEvent(id, event));
            if (bucket.tryConsume(events.size())) {
                return ResponseEntity.of(Optional.of(new EventsResults(events)));
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
            event.notifySubscribesEventDelete();
            return ResponseEntity.ok().build();
        }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping(path = "/users/{id}/deleteMultiplyEvents")
    public ResponseEntity<User> deleteMultiplyEvents(@RequestBody @Valid List<Long> eventIds){
        try {
            List<Event> events = eventJpaService.findEvents(eventIds);
            events.forEach(this::deleteEvent);
            return ResponseEntity.status(HttpStatus.OK).build();

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
                event.notifySubscribesEventUpdated();
                return ResponseEntity.of(Optional.of(event));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(path = "/users/{id}/updateMultiplyEvents")
    public ResponseEntity<EventsResults> updateMultiplyEvent(@PathVariable long id,@RequestBody @Valid List<Event> events) {
        try {
            events.forEach(event -> updateEvent(id, event));
            return ResponseEntity.of(Optional.of(new EventsResults(events)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
