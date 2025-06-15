package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @NotNull @RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription().isBlank()) {
            throw new IncorrectDataException("Description должен быть заполнен");
        }
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOtherUserRequest(@Valid @NotNull @RequestParam("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getAllOtherUserRequest(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequest(@Valid @NotNull @PathVariable("id") Long requestId) {
        return itemRequestClient.getDetailedRequestById(requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequests(@Valid @NotNull @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestClient.getDetailedRequestsAllByUserId(userId);
    }
}
