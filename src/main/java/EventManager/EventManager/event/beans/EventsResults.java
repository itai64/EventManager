package EventManager.EventManager.event.beans;

import EventManager.EventManager.jpa.beans.Event;

import java.util.List;

public class EventsResults {
    private final List<Event> events;

    public EventsResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
