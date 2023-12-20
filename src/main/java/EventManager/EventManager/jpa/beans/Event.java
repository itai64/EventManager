package EventManager.EventManager.jpa.beans;

import jakarta.persistence.*;
import org.hibernate.type.descriptor.DateTimeUtils;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.Date;

@Entity()
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue
    private final int id;

    private final String description;

    private int popularity;

    private Date eventDate;
    private final Date creationTime;

    private String location;

    public Event(int id, String description, int popularity,Date eventDate,Date creationTime) {
        this.id = id;
        this.description = description;
        this.popularity = popularity;
        this.eventDate = eventDate;
        this.creationTime=creationTime;
    }

    public int getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }
    public int getPopularity() {
        return popularity;
    }
    public Date getEventDate(){return eventDate;}
    public String getLocation(){return location;}
    public Date getCreationTime(){return creationTime;}
    public void setPopularity(int popularity){this.popularity = popularity;}
    public void setLocation(String location){this.location=location;}

    public void setEventDate(Date eventDate){
        this.eventDate= eventDate;
    }

    public static Comparator<Event> compareEventsByDate= new Comparator<>() {
        @Override
        public int compare(Event event,Event otherEvent) {
            return Long.compare(event.getEventDate().getTime(),otherEvent.eventDate.getTime());
        }
    } ;

    public static Comparator<Event> compareEventsByPopularity= new Comparator<>() {
        @Override
        public int compare(Event event,Event otherEvent) {
            return Integer.compare(event.getPopularity(),otherEvent.getPopularity());
        }
    } ;

    public static Comparator<Event> compareEventsByCreationTime= new Comparator<>() {
        @Override
        public int compare(Event event,Event otherEvent) {
            return Long.compare(event.getCreationTime().getTime(),otherEvent.getCreationTime().getTime());
        }
    } ;
}
