package EventManager.EventManager.jpa.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity()
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private final long id;
    @Size(min=2,message = "Name should have at least 2 characters")
    private final String name;

    @OneToMany(mappedBy = "user",cascade =  { CascadeType.REMOVE })
    @JsonIgnore()
    private List<Event> events;

    public User(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public long getId() {
        return id;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event){
        this.events.add(event);
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
