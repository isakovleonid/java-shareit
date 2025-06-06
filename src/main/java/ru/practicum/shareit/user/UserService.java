package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.IncorrectDataException;
import ru.practicum.shareit.handler.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(Long id) {
        return Optional.of(userRepository.findById(id)).get().orElseThrow(() -> new NotFoundException("Пользователь id = " + id + " не найден"));
    }

    public User add(@Valid User user) {
        if (user.getEmail() == null || user.getEmail().isBlank())
            throw new IncorrectDataException("Email не может быть пустым");

        if (user.getName() == null || user.getName().isBlank())
            throw new IncorrectDataException("Name не может быть пустым");

        return userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public User update(User updUser) {
        User existingUser = Optional.of(userRepository.findById(updUser.getId())).get().orElseThrow(() -> new NotFoundException("Пользователь id = " + updUser.getId() + " не найден"));

        User newUser = existingUser.toBuilder()
                .name(updUser.getName() != null ? updUser.getName() : existingUser.getName())
                .email(updUser.getEmail() != null ? updUser.getEmail() : existingUser.getEmail())
                .build();

        return userRepository.save(newUser);
    }
}
