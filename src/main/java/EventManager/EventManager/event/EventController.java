package EventManager.EventManager.event;

import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.user.UserJpaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("event/")
@RestController
public class EventController {

    private final UserJpaService userJpaService;
    private final EventJpaService eventJpaService;

    public EventController(UserJpaService userJpaService, EventJpaService eventJpaService) {
        this.userJpaService = userJpaService;
        this.eventJpaService = eventJpaService;
    }

    @GetMapping(path="/getAllEvents")
    public List<Event> getAllEvents(){
        return eventJpaService.retrieveAllPosts();
    }}


