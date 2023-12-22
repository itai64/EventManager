package EventManager.EventManager.user;

import EventManager.EventManager.event.EventJpaService;
import EventManager.EventManager.user.beans.EventsResults;
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
import java.util.*;

@RestController
public class UserController {
    private final UserJpaService userJpaService;
    private final EventJpaService eventJpaService;
    private final EventRemainderService eventRemainderService;
    private final Bucket bucket;
    private final Map<Long,List<Subscriber>> subscribers;

    public UserController(UserJpaService userJpaService, EventJpaService eventJpaService, EventRemainderService eventRemainderService) {
        this.userJpaService = userJpaService;
        this.eventJpaService = eventJpaService;
        this.eventRemainderService = eventRemainderService;
        Bandwidth limit = Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
        this.subscribers = new HashMap<>();
    }

    /**
     * @param id
     * @return
     *
     * use to find user by id and return his data. return the user data
     */
    @GetMapping(path = "/users/{id}")
    public EntityModel<User> findUserById(@PathVariable int id){
        return EntityModel.of(userJpaService.findUser(id));
    }

    /**
     * @param user
     * @return User
     *
     * Allow clients to create a new user. return the new user data.
     */

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

    /**
     * @param id
     * @return User
     *
     * use to delete user. return the deleted user
     */

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

    /**
     * @param id
     * @return List of events
     *
     * use to find all user event's. return all user events.
     *
     */

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

    /**
     *
     * @param id
     * @return list of user event
     *
     * use to get all user event sort by creationTime. return the relevant events.
     *
     */

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

    /**
     *
     * @param id
     * @return list of user event
     *
     * use to get all user event sort by event date. return the relevant event.
     *
     */

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

    /**
     *
     * @param id
     * @return list of user event
     *
     * use to get all user event sort by popularity. return the relevant events.
     *
     */
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

    /**
     *
     * @param id
     * @param location
     * @return zc
     *
     * use to get all user event in specific location. return all relevant use events.
     *
     */

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

    /**
     *
     * @param id
     * @param event
     * @return event
     *
     * Allow user to create event. return the created event.
     */

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

    /**
     *
     * @param id
     * @param events
     * @return List<Event>
     *
     * Allow user to create multiply event. return the created events.
     */

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

    /**
     *
     * @param id
     * @param eventId
     * @return event
     *
     * Allow user to get event by id. return the selected event
     */

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

    /**
     *
     * @param event
     *
     *
     *Allow user to delete event
     */

    @DeleteMapping(path = "/users/{id}/deleteEvent")
    public ResponseEntity<User> deleteEvent(@RequestBody @Valid Event event){
        try {
        if (bucket.tryConsume(1)) {
            eventJpaService.deleteEvent(event.getId());
            eventRemainderService.deleteRemainder(event);
            notifySubscribers(event.getId(),"event deleted");
            return ResponseEntity.ok().build();
        }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     *
     * @param eventIds
     *
     *
     *Allow user to delete multiply events.
     */

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

    /**
     *
     * @param id
     * @param event
     * @return event
     *
     * Allow user to update event. return the updated event.
     */

    @PostMapping(path = "/users/{id}/updateEvent")
    public ResponseEntity<Event> updateEvent(@PathVariable long id,@RequestBody @Valid Event event){
        try {
            if (bucket.tryConsume(1)) {
                eventJpaService.updateEvent(event);
                eventRemainderService.updateRemainder(id, event);
                notifySubscribers(event.getId(),"event update");
                return ResponseEntity.of(Optional.of(event));
            }
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e){
        e.printStackTrace();
        return ResponseEntity.internalServerError().build();
        }
    }

    /**
     *
     * @param id
     * @param events
     * @return event
     *
     * Allow user to update multiply events. return the updated events.
     */


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

    /**
     *
     * @param userId
     * @param eventId
     *
     * Allow to user subscribe to other user event.
     */

    @PostMapping(path = "/users/{userId}/subscribe/{eventId}")
    public void addSubscribe(@PathVariable long userId, @PathVariable long eventId) {

        try {
            userJpaService.findUser(userId);
            Optional<Event> event = eventJpaService.findEvent(eventId);

            if (event.isEmpty()) {
                throw new Exception("event " + eventId + " not found");
            }

            addSubscribe(eventId,new ConsoleSubscriber(userId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @param userId
     * @param eventId
     *
     * Allow to user subscribe to other user event.
     */

    @PostMapping(path = "/users/{userId}/removeSubscribe/{eventId}")
    public void removeSubscribe(@PathVariable long userId, @PathVariable long eventId) {

        try {
            userJpaService.findUser(userId);
            Optional<Event> event = eventJpaService.findEvent(eventId);

            if (event.isEmpty()) {
                throw new Exception("event " + eventId + " not found");
            }

            removeSubscribe(eventId,new ConsoleSubscriber(userId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void addSubscribe(long eventId, Subscriber subscriber) {
        List<Subscriber> subscribersList;
        if (this.subscribers.containsKey(eventId)){
            subscribersList=this.subscribers.get(eventId);
        }else {
            subscribersList = new ArrayList<>();
        }
        subscribersList.add(subscriber);
        this.subscribers.put(eventId,subscribersList);
    }

    public void removeSubscribe(long eventId, Subscriber subscriber) {
        if (!this.subscribers.containsKey(eventId)) {
            return;
        }
        List<Subscriber> subscribersList = this.subscribers.get(eventId);
        subscribersList.remove(subscriber);
        this.subscribers.put(eventId,subscribersList);
    }

    private void notifySubscribers(long eventId,String message) {
        List<Subscriber> subscribers= this.subscribers.get(eventId);
        if (subscribers == null){
            return;
        }
        for (Subscriber subscriber : subscribers) {
            subscriber.notify(message);
        }
    }

}
