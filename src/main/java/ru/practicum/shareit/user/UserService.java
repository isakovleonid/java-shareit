package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(Long id) {
        return userRepository.getById(id);
    }

    public User add(@Valid User user) {
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new RuntimeException("Email не может быть пустым");

        if (user.getName() == null || user.getName().isBlank())
            throw new RuntimeException("Name не может быть пустым");

        return userRepository.add(user);
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }

    public User update(Long id, User user) {
        return userRepository.update(id, user);
    }
}
