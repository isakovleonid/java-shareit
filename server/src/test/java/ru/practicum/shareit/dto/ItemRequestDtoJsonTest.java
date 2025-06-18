package ru.practicum.shareit.dto;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequest = new ItemRequestDto(1L,
                "Запрос предмета",
                LocalDateTime.parse("2023-12-01T12:53:00", DateTimeFormatter.ISO_DATE_TIME)
        );

        JsonContent<ItemRequestDto> result = json.write(itemRequest);

        assertThat(result).isEqualTo("{\"id\":1," +
                "\"description\":\"Запрос предмета\"," +
                "\"created\":\"2023-12-01T12:53:00\"" +
                "}");
    }
}
