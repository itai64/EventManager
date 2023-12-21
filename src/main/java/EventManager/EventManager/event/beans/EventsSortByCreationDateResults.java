package EventManager.EventManager.event.beans;

import EventManager.EventManager.jpa.beans.Event;

import java.util.List;

public class EventsSortByCreationDateResults {

    private final List<Event> events;

    public EventsSortByCreationDateResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
