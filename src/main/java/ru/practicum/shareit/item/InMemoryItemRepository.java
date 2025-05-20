package ru.practicum.shareit.item;

import jakarta.validation.*;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
@Validated
public class InMemoryItemRepository implements ItemRepository {
    Map<Long, Item> itemMap = new HashMap<>();
    private Long maxId = 0L;

    Long nextId() {
        return maxId++;
    }

    @Override
    public Item add(Item item, User owner) {
        Item newItem = item.toBuilder().build();

        newItem.setOwner(owner);
        newItem.setId(this.nextId());

        save(newItem);

        return newItem;
    }

    @Override
    public void delete(Long id) {
        itemMap.remove(id);
    }

    @Override
    public List<Item> getAll() {
        return itemMap.values().stream().toList();
    }

    @Override
    public Item getById(Long id) {
        return Optional.of(itemMap.get(id)).orElseThrow(() -> new NotFoundException("Предмет не найден"));
    }

    @Override
    public List<Item> getAllByUser(User user) {
        return itemMap.values().stream()
                .filter(i -> (user == null || i.getOwner().equals(user)))
                .toList();
    }

    @Override
    public List<Item> search(User user, String desc) {
        return itemMap.values().stream()
                .filter(i -> (user == null || i.getOwner().equals(user)))
                .filter(Item::getAvailable)
                .filter(i -> desc == null || i.getDescription().toLowerCase().contains(desc.toLowerCase()) || i.getName().toLowerCase().contains(desc.toLowerCase()))
                .toList();
    }

    @Override
    public Item update(Long id, Item updItem, User owner) {
        Item item = getById(id);

        if (updItem.getAvailable() != null) {
            item.setAvailable(updItem.getAvailable());
        }

        if (updItem.getName() != null) {
            item.setName(updItem.getName());
        }

        if (updItem.getDescription() != null) {
            item.setDescription(updItem.getDescription());
        }

        if (updItem.getRequest() != null) {
            item.setRequest(updItem.getRequest());
        }

        save(item);

        return item;
    }

    @Validated
    void save(@Valid Item item) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<Item>> violations = factory.getValidator().validate(item);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        itemMap.put(item.getId(), item);
    }
}
