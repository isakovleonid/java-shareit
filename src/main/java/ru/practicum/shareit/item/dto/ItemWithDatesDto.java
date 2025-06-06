package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemWithDatesDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;

    private List<CommentDto> comments;
}
