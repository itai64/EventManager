package EventManager.EventManager.jpa.beans;

import jakarta.persistence.*;

import java.time.LocalDateTime;
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

    public Event(int id, String description, int popularity,LocalDateTime eventDate) {
        this.id = id;
        this.description = description;
        this.popularity = popularity;
        this.eventDate = eventDate;
        this.creationTime= LocalDateTime.now();
    }
    public Event(){
        this.eventDate = LocalDateTime.now();

        this.creationTime=LocalDateTime.now();
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
