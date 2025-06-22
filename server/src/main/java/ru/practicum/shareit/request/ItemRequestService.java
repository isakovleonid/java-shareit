package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDetailDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemRequest> getAllOtherUserRequest(Long userId) {
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
    }

    public ItemRequest add(Long userId, @Valid ItemRequest itemRequest) {
        User requester = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        itemRequest.setRequestor(requester);
        itemRequest.setCreated(now);

        return itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequestDetailDto> getDetailedRequestsAll(Long userId) {
        return itemRequestRepository.findAllRequestsWithItems(userId);
    }

    public ItemRequestDetailDto getDetailedRequestById(Long id) {
        return itemRequestRepository.findRequestWithItems(id);
    }
}
