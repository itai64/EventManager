package EventManager.EventManager.remainder;

import EventManager.EventManager.jpa.beans.Event;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service
public class EventRemainderService {

    Map <Long,ScheduledFuture<?>> executorServicesMap = new HashMap<>();

    ScheduledExecutorService executorService= Executors.newScheduledThreadPool(20);

    private final static long MIN_TIME_FOR_REMAINDER = 30;

    public void runRemainder(long userId,Event event) {
        long timeToEventInSec = ChronoUnit.MINUTES.between(LocalDateTime.now(),event.getEventDate() );
        if (timeToEventInSec  <= MIN_TIME_FOR_REMAINDER){
            System.out.println(timeToEventInSec +" minutes is not valid time for remainder");
            executorServicesMap.remove(event.getId());
            return;
        }

//        TimerTask task = new TimerTask() {
//            public void run() {
//                System.out.println("User "+ userId +"have upcoming event "+ event.getId() +" performed on: " + new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)) + "n" +
//                        "event's description is: " + event.getDescription());
//            }
//        };
//        Timer timer = new Timer("Timer");
//        timers.put(event.getId(),timer);
//        timer.schedule(task,ChronoUnit.MILLIS.between(LocalDateTime.now(),event.getEventDate().minusMinutes(30)));


        Runnable runTask = ()->{
            System.out.println("User "+ userId +"have upcoming event "+ event.getId() +" performed on: " + new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30)) + "n" +
                    "event's description is: " + event.getDescription());};

        ScheduledFuture<?> schedule = executorService.schedule(runTask, ChronoUnit.MILLIS.between(LocalDateTime.now(), event.getEventDate().minusMinutes(30)), TimeUnit.MILLISECONDS);
        executorServicesMap.put(event.getId(),schedule);
//
    }

    public void updateRemainder(long userId,Event event){
        deleteRemainder(event);
        runRemainder(userId,event);
    }

    public void deleteRemainder(Event event){

        ScheduledFuture<?> schedule = executorServicesMap.get(event.getId());
        if (schedule != null){
            executorServicesMap.remove(event.getId());
            schedule.cancel(true);
        }
    }
}
