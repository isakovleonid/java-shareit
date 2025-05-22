package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRepository {
    Item getById(Long id);

    List<Item> getAll();

    List<Item> getAllByUser(User user);

    Item add(Item item, User owner);

    Item update(Long id, Item item, User user);

    void delete(Long id);

    List<Item> search(User user, String desc);
}
