package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L,
                "Предмет",
                "Описание предмета",
                true,
                null,
                LocalDateTime.parse("2023-12-01T12:53:00", DateTimeFormatter.ISO_DATE_TIME),
                null,
                null
                );

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).isEqualTo("{\"id\":1," +
                "\"name\":\"Предмет\"," +
                "\"description\":\"Описание предмета\"," +
                "\"available\":true," +
                "\"requestId\":null," +
                "\"lastBooking\":\"2023-12-01T12:53:00\"," +
                "\"nextBooking\":null," +
                "\"comments\":null}");
    }
}
