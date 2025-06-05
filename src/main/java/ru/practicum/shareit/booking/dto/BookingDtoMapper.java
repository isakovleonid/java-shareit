package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.mapper.DTOMapper;

@Component
public class BookingDtoMapper implements DTOMapper<BookingDto, Booking> {
    @Override
    public Booking fromDTO(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), bookingDto.getItem(), bookingDto.getBooker(), bookingDto.getStatus());
    }

    @Override
    public BookingDto toDTO(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getItem(), booking.getBooker(), booking.getStatus());
    }
}
