package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.mapper.DTOMapper;
import ru.practicum.shareit.user.dto.UserDtoMapper;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper implements DTOMapper<BookingDto, Booking> {
    private final ItemDtoMapper itemDtoMapper;
    private final UserDtoMapper userDtoMapper;

    @Override
    public Booking fromDTO(BookingDto bookingDto) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), itemDtoMapper.fromDTO(bookingDto.getItem()), userDtoMapper.fromDTO(bookingDto.getBooker()), bookingDto.getStatus());
    }

    @Override
    public BookingDto toDTO(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(), itemDtoMapper.toDTO(booking.getItem()), userDtoMapper.toDTO(booking.getBooker()), booking.getStatus());
    }
}
