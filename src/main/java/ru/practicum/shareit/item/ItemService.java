package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.OtherUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public Item getById(Long id) {
        return itemRepository.getById(id);
    }

    public Item add(Item item, User owner) {
        return itemRepository.add(item, owner);
    }

    public Item update(Long id, Item updItem, User user) {
        Item item = itemRepository.getById(id);
        if (!checkOwner(item, user)) {
            throw new OtherUserException("Нельзя обновлять предметы другого пользователя");
        }

        return itemRepository.update(id, updItem, user);
    }

    public List<Item> search(User user, String text) {
        if (text == null) {
            return new ArrayList<>();
        }

        return itemRepository.search(user, text);
    }

    public List<Item> getAllByUser(User user) {
        return itemRepository.getAllByUser(user);
    }

    boolean checkOwner(Item item, User user) {
        return item.getOwner().equals(user);
    }
}
