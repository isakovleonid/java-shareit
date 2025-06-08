package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;
    private final CommentDtoMapper commentDtoMapper;

    @GetMapping
    public List<ItemWithDatesDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader("X-Sharer-User-Id") Long ownerId, @NotNull @RequestParam("text") String text) {
        if (text == null) {
            return new ArrayList<>();
        }

        return itemService.search(ownerId, text).stream()
                .map(itemDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable("id") Long id) {
        return itemService.getById(id);
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody ItemDto itemDto) {
        Item item = itemDtoMapper.fromDTO(itemDto);

        return itemDtoMapper.toDTO(itemService.add(ownerId, item));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable("id") Long itemId, @RequestBody ItemDto itemDto) {
        Item updIitem = itemDtoMapper.fromDTO(itemDto);
        return itemDtoMapper.toDTO(itemService.update(ownerId, itemId, updIitem));
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable("id") Long itemId, @RequestBody CommentDto comment) {
        return commentDtoMapper.toDTO(itemService.addComment(itemId, authorId, comment.getText()));
    }

}
