package EventManager.EventManager.event.beans;

import EventManager.EventManager.jpa.beans.Event;

import java.util.List;

public class EventsSortByPopularityResults {
    private final List<Event> events;

    public EventsSortByPopularityResults(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }
}
