package EventManager.EventManager.event.beans;

import EventManager.EventManager.jpa.beans.Event;

import java.util.List;

public class EventsSortByEventDateResults {

    private final List<Event> events;

    public EventsSortByEventDateResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
