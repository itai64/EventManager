package EventManager.EventManager.event.beans;

import EventManager.EventManager.jpa.beans.Event;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

public class EventByLocationResults {

   private final List<Event> events;

    public EventByLocationResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
