package EventManager.EventManager.remainder;

import EventManager.EventManager.jpa.beans.Event;
import EventManager.EventManager.jpa.beans.User;

import java.sql.Time;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class EventRemainderService {

    Map <Integer,Timer> timers = new HashMap<>();

    private final static long MIN_TIME_FOR_REMAINDER = 30;

    public void runRemainder(long userId,Event event) {
        long timeToEventInSec = TimeUnit.MILLISECONDS.toMinutes(event.getEventDate().getTime() - System.currentTimeMillis());
        if (timeToEventInSec  <= MIN_TIME_FOR_REMAINDER){
            System.out.println(timeToEventInSec +" minutes is not valid time for remainder");
            return;
        }

        TimerTask task = new TimerTask() {
            public void run() {
                System.out.println("User "+ userId +"have upcoming event "+ event.getId() +" performed on: " + new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)) + "n" +
                        "event's description is: " + event.getDescription());
            }
        };
        Timer timer = new Timer("Timer");
        timers.put(event.getId(),timer);

        timer.schedule(task, new Date(event.getEventDate().getTime() - TimeUnit.MINUTES.toMillis(30)));
    }

    public void updateRemainder(long userId,Event event){
        deleteRemainder(event);
        runRemainder(userId,event);
    }

    public void deleteRemainder(Event event){
        Timer timer = timers.get(event.getId());
        timers.remove(event.getId());
        timer.cancel();
    }
}
