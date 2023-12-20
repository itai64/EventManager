package EventManager.EventManager.exceptions.beans;

import java.time.LocalDateTime;

public record ErrorDetails(String message, LocalDateTime timeStamp, String details) {
}
