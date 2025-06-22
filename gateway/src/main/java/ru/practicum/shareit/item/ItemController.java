package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return itemClient.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @NotNull @RequestParam("text") String text) {
        if (text == null) {
            return null;
        }

        return itemClient.search(ownerId, text);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                          @NotNull @PathVariable("id") Long id) {
        return itemClient.getById(userId, id);
    }

    @PostMapping
    public ResponseEntity<Object> add(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @Valid @RequestBody ItemDto itemDto) {
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @Valid @PathVariable("id") Long itemId,
                                         @RequestBody ItemUpdateDto itemDto) {
        return itemClient.update(ownerId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long authorId, @PathVariable("id") Long itemId, @RequestBody CommentDto comment) {
        return itemClient.addComment(itemId, authorId, comment);
    }

}
