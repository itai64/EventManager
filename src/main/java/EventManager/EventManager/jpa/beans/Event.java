package EventManager.EventManager.jpa.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.google.common.eventbus.EventBus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.EventListener;

@Entity()
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue
    private long id;
    private  String description;
    private int popularity;
    private LocalDateTime eventDate;
    private final LocalDateTime creationTime;
    private String location;
    @Transient
    EventBus eventBus ;

    public Event(int id, String description, int popularity,LocalDateTime eventDate) {
        this.id = id;
        this.description = description;
        this.popularity = popularity;
        this.eventDate = eventDate;
        this.creationTime= LocalDateTime.now();
        this.eventBus = new EventBus();
    }
    public Event(){
        this.eventDate = LocalDateTime.now();
        this.creationTime=LocalDateTime.now();
        this.eventBus = new EventBus();
    }

    public void notifySubscribesEventDelete(){
        eventBus.post("Event" + id+" deleted.");
    }

    public void notifySubscribesEventUpdated(){
        eventBus.post("Event" + id+" updated.");
    }

    public void addListener(EventListener listener){
        eventBus.register(listener);
    }

    public long getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public int getPopularity() {
        return popularity;
    }
    public LocalDateTime getEventDate(){return eventDate;}
    public String getLocation(){return location;}
    public LocalDateTime getCreationTime(){return creationTime;}
    public void setPopularity(int popularity){this.popularity = popularity;}
    public void setLocation(String location){this.location=location;}

    public void setEventDate(LocalDateTime eventDate){
        this.eventDate= eventDate;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", popularity=" + popularity +
                ", eventDate=" + eventDate +
                ", creationTime=" + creationTime +
                ", location='" + location + '\'' +
                '}';
    }
}
