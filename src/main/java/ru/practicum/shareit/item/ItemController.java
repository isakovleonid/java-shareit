package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemDtoMapper itemDtoMapper;

    @GetMapping
    public List<ItemDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        User owner = userService.getById(ownerId);
        return itemService.getAllByUser(owner).stream()
                .map(itemDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long ownerId, @NotNull @RequestParam("text") String text) {
        User owner = userService.getById(ownerId);
        return itemService.search(owner, text).stream()
                .map(itemDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long id) {
        return itemDtoMapper.toDTO(itemService.getById(id));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto) {
        User owner = userService.getById(ownerId);
        Item item = itemDtoMapper.fromDTO(itemDto);

        return itemDtoMapper.toDTO(itemService.add(item, owner));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable("id") Long itemId, @RequestBody ItemDto itemDto) {
        User owner = userService.getById(ownerId);
        Item updIitem = itemDtoMapper.fromDTO(itemDto);

        return itemDtoMapper.toDTO(itemService.update(itemId, updIitem, owner));
    }



}
