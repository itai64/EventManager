package EventManager.EventManager.event;

import EventManager.EventManager.jpa.beans.Event;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("event/")
@RestController
public class EventController {
    private final EventJpaService eventJpaService;



    public EventController(EventJpaService eventJpaService) {

        this.eventJpaService = eventJpaService;
    }

    /**
     *
     * @return list of event
     *
     * allow to get all users events. return list of events.
     */
    @GetMapping(path="/getAllEvents")
    public List<Event> getAllEvents(){
        return eventJpaService.retrieveAllEvents();
    }}


