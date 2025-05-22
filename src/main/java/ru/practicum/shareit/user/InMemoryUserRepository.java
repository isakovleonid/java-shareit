package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.NotFoundException;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Validated
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long maxId = 0L;

    Long nextId() {
        return maxId++;
    }

    @Override
    public List<User> getAll() {
        return userMap.values().stream().toList();
    }

    @Override
    public User getById(Long id) {
        return Optional.ofNullable(userMap.get(id)).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public void delete(Long id) {
        userMap.remove(id);
    }

    @Override
    public User add(User user) {
        User newUser = user.toBuilder().build();
        newUser.setId(this.nextId());

        save(newUser);

        return newUser;
    }

    @Override
    public User update(Long id, User updUser) {
        User user = getById(id);

        if (updUser.getEmail() != null) {
            user.setEmail(updUser.getEmail());
        }

        if (updUser.getName() != null) {
            user.setName(updUser.getName());
        }

        save(user);

        return user;
    }

    @Validated
    private void save(@Valid User user) {
        Optional<User> dublicateUser = userMap.values().stream()
                .filter(u -> Objects.equals(u.getEmail(), user.getEmail()) && !u.getId().equals(user.getId()))
                .findFirst();

        if (dublicateUser.isPresent()) {
                throw new RuntimeException("Email " + user.getEmail() + " уже используется");
        }

        userMap.put(user.getId(), user);
    }
}
