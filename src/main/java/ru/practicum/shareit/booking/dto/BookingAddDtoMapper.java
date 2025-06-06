package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;

@Component
public class BookingAddDtoMapper {
    public Booking fromDTO(BookingAddDto bookingAddDto) {
        return new Booking(null, bookingAddDto.getStart(), bookingAddDto.getEnd(), null, null, null);
    }
}
