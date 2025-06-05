package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentDtoMapper;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithDatesDto;
import ru.practicum.shareit.item.exception.OtherUserException;
import ru.practicum.shareit.item.exception.UnvailableItemExecption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CommentDtoMapper commentDtoMapper;

    public ItemDto getById(Long id) {
        LocalDateTime currDate = LocalDate.now().atStartOfDay();
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет с id = " + id + " не найден"));

        Booking lastBooking = bookingRepository.findLastUpToDateByItem(item.getId(), currDate);
        Booking nextBooking = bookingRepository.findNextToDateByItem(item.getId(), currDate.plusDays(1));

        List<CommentDto> commentsDto = commentRepository.findByItemId(item.getId()).stream()
                .map(commentDtoMapper::toDTO)
                .toList();

        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                lastBooking != null ? lastBooking.getEnd() : null,
                nextBooking != null ? nextBooking.getStart() : null,
                commentsDto);
    }

    public Item add(Long ownerId, Item item) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        item.setOwner(owner);
        return itemRepository.save(item);
    }

    public Item update(Long ownerId, Long itemId, Item updItem) {
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id = " + itemId + " не найден"));
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        if (!checkOwner(existingItem, owner)) {
            throw new OtherUserException("Нельзя обновлять предметы другого пользователя");
        }

        if (updItem.getName() == null) {
            updItem.setName(existingItem.getName());
        }

        if (updItem.getAvailable() == null) {
            updItem.setAvailable(existingItem.getAvailable());
        }

        if (updItem.getRequest() == null) {
            updItem.setRequest(existingItem.getRequest());
        }

        if (updItem.getDescription() == null) {
            updItem.setDescription(existingItem.getDescription());
        }

        return itemRepository.save(updItem);
    }

    public List<Item> search(Long ownerId, String text) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));
        return itemRepository.search(owner, text);
    }

    public List<ItemWithDatesDto> getAllByOwner(Long ownerId) {
        LocalDateTime currDate = LocalDate.now().atStartOfDay();
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + ownerId + " не найден"));

        List<Item> itemsByUser = itemRepository.findByOwner(owner);

        return itemsByUser.stream()
                .map(
                        item -> {
                            Booking lastBooking = bookingRepository.findLastUpToDateByItem(item.getId(), currDate);
                            Booking nextBooking = bookingRepository.findNextToDateByItem(item.getId(), currDate.plusDays(1));

                            List<CommentDto> commentsDto = commentRepository.findByItemId(item.getId()).stream()
                                    .map(commentDtoMapper::toDTO)
                                    .toList();

                            return new ItemWithDatesDto(item.getId(),
                                    item.getName(),
                                    item.getDescription(),
                                    item.getAvailable(),
                                    lastBooking != null ? lastBooking.getEnd() : null,
                                    nextBooking != null ? nextBooking.getStart() : null,
                                    commentsDto);
                        }
                ).toList();
    }

    boolean checkOwner(Item item, User user) {
        return item.getOwner().equals(user);
    }

    public Comment addComment(Long itemId, Long authorId, String text) {
        LocalDateTime currDate = LocalDateTime.now();

        List<Booking> endedBookings = bookingRepository.findByItemIdAndBookerIdAndEndLessThanEqual(itemId, authorId, currDate);

        if (endedBookings.isEmpty()) {
            throw new UnvailableItemExecption("Нет завершенных бронирований у пользователя id = " + authorId + " предмета id = " + itemId);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + itemId + " не найден"));
        User author = userRepository.findById(authorId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + authorId + " не найден"));

        return commentRepository.save(new Comment(null, text, item, author, currDate));
    }
}
