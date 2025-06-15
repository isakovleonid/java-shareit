package ru.practicum.shareit.comment.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.Comment;

@Component
public class CommentDtoMapper {
    public CommentDto toDTO(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getItem().getId(), comment.getAuthor().getName(), comment.getCreated());
    }
}
