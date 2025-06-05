package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.AllowedValue.AllowedValues;
import ru.practicum.shareit.handler.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.OtherUserException;
import ru.practicum.shareit.item.exception.UnvailableItemExecption;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new NotFoundException("Заявка с id = " + id + "не найдена"));
    }

    public Booking getById(Long id, Long ownerId) {
        Booking booking = findBookingById(id);
        if (!checkUser(booking, ownerId)) {
            throw new OtherUserException("Нельзя Запрашивать заявки предметов другого пользователя");
        }
        return booking;
    }

    private boolean checkUser(Booking booking, Long userId) {
        return (booking.getItem().getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId));
    }

    public Booking add(Booking booking, Long itemId, Long bookerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id = " + itemId + " не найден"));
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден"));
        booking.setBooker(booker);
        booking.setItem(item);

        if (!booking.getItem().getAvailable()) {
            throw new UnvailableItemExecption("Предмет с id = " + booking.getItem().getId() + " недоступен");
        }

        return bookingRepository.save(booking);
    }

    public Booking approve(Long bookingId, Long userId, boolean approved) {
        Booking booking = findBookingById(bookingId);

        if (!checkUser(booking, userId)) {
            throw new OtherUserException("Нельзя подтверждать заявки предметов другого пользователя");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        return bookingRepository.save(booking);
    }

    public List<Booking> getByBookerId(Long bookerId, @AllowedValues({"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"}) String state) {
        User booker = userRepository.findById(bookerId).orElseThrow(() -> new NotFoundException("Пользователь с id = " + bookerId + " не найден"));

        LocalDateTime currDate = LocalDateTime.now();

        if (state.equals("ALL")) {
            return bookingRepository.findByBookerIdOrderByStartDesc(booker.getId());
        } else if (state.equals("CURRENT")) {
            return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), currDate, currDate);
        } else if (state.equals("PAST")) {
            return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), currDate);
        } else if (state.equals("FUTURE")) {
            return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), currDate);
        } else if (state.equals("WAITING")) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING);
        } else if (state.equals("REJECTED")) {
            return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED);
        }

        return new ArrayList<>();
    }
}
