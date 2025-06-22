package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AllArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoJsonTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testBookingDtoJson() throws IOException {
        ItemDto itemDto = new ItemDto(1L,
                "Предмет",
                "Описание предмета",
                true,
                null,
                LocalDateTime.parse("2023-12-01T12:53:00", DateTimeFormatter.ISO_DATE_TIME),
                null,
                null
        );

        UserDto userDto = new UserDto(1L, "Тестовый пример", "1@abc.com");

        BookingDto bookingDto = new BookingDto(1L,
                LocalDateTime.parse("2025-01-01T12:14:16", DateTimeFormatter.ISO_DATE_TIME),
                null,
                itemDto,
                userDto,
                BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).isEqualTo("{\"id\":1," +
                "\"start\":\"2025-01-01T12:14:16\"," +
                "\"end\":null," +
                "\"item\":{\"id\":1,\"name\":\"Предмет\",\"description\":\"Описание предмета\",\"available\":true,\"requestId\":null,\"lastBooking\":\"2023-12-01T12:53:00\",\"nextBooking\":null,\"comments\":null}," +
                "\"booker\":{\"id\":1,\"name\":\"Тестовый пример\",\"email\":\"1@abc.com\"}," +
                "\"status\":WAITING" +
                "}");
    }
}
