package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingAddDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingDtoMapper bookingDtoMapper;
    private final BookingAddDtoMapper bookingAddDtoMapper;

    @GetMapping("/{id}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long bookingId) {
        return bookingDtoMapper.toDTO(bookingService.getById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getByIdState(@RequestHeader("X-Sharer-User-Id") Long bookerId, @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getByBookerId(bookerId, state).stream()
                .map(bookingDtoMapper::toDTO)
                .toList();
    }

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long bookerId, @RequestBody BookingAddDto bookingAddDto) {
        Booking booking = bookingAddDtoMapper.fromDTO(bookingAddDto);
        booking.setStatus(BookingStatus.WAITING);

        return bookingDtoMapper.toDTO(bookingService.add(booking, bookingAddDto.getItemId(), bookerId));
    }

    @PatchMapping("/{id}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable("id") Long bookingId,
                              @RequestParam("approved") boolean approved) {
        return bookingDtoMapper.toDTO(bookingService.approve(bookingId, ownerId, approved));
    }
}
