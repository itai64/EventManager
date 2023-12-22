package EventManager.EventManager.jpa.beans;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity()
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private  long id;
    @Size(min=2,message = "Name should have at least 2 characters")
    private  String name;
    private List<Long> events;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
        this.events=new ArrayList<>();
    }

    @Subscribe
    @AllowConcurrentEvents
    public void eventNotification(String message){
        System.out.println("event changed" + message);
    }
    public User(){this.events=new ArrayList<>();}

    public void setEvents(List<Long> events) {
        this.events = events;
    }

    public long getId() {
        return id;
    }

    public List<Long> getEventsIds() {
        return events;
    }

    public void addEvent(Event event){
        this.events.add(event.getId());
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", events=" + events +
                '}';
    }
}
