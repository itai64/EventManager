package EventManager.EventManager.event.beans;

import org.springframework.stereotype.Component;

@Component
public class ConsoleSubscriber implements Subscriber {
    private long userId ;

    public ConsoleSubscriber(long userId) {
        this.userId = userId;
    }

    public ConsoleSubscriber() {}

    @Override
    public void notify(String eventState) {
        System.out.println("Notified userId "+userId+"of event change: " + eventState);
    }
}
