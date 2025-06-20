package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.IncorrectDataException;
import ru.practicum.shareit.request.dto.ItemRequestDetailDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemRequestDtoMapper itemRequestDtoMapper;

    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = itemRequestDtoMapper.fromDto(itemRequestDto);

        if (userId == null)
            throw new IncorrectDataException("Заявитель не может быть пустым");

        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank())
            throw new IncorrectDataException("Описание не может быть пустым");

        return itemRequestDtoMapper.toDto(itemRequestService.add(userId, itemRequest));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllOtherUserRequest(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllOtherUserRequest(userId).stream()
                .map(itemRequestDtoMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ItemRequestDetailDto getRequest(@PathVariable Long id) {
        return itemRequestService.getDetailedRequestById(id);
    }

    @GetMapping
    public List<ItemRequestDetailDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getDetailedRequestsAll(userId);
    }
}
