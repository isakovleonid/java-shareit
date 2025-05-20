package ru.practicum.shareit.user;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User getById(Long id);

    User add(User user);

    User update(Long id, User user);

    void delete(Long id);
}
