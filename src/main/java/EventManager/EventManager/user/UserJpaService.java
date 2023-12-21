package EventManager.EventManager.user;

import EventManager.EventManager.exceptions.UserNotFoundException;
import EventManager.EventManager.jpa.UserRepository;
import EventManager.EventManager.jpa.beans.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserJpaService {

    private final UserRepository userRepository;

    public UserJpaService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUser(long id) {
        var user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("user " + id + " not found");
        }
        return user.get();
    }

    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
    public User createUser(User user){
        return userRepository.save(user);
    }

    public void updateUserEvents(long userId,List<Long> eventIds){
        userRepository.updateUserEvent(userId,eventIds);
    }
}
