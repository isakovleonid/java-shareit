package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllByOwner(Long ownerId) {
       return get("", ownerId);
    }

    public ResponseEntity<Object> search(@NotNull Long ownerId, @NotNull String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("", ownerId, parameters);
    }

    public ResponseEntity<Object> getById(@NotNull Long userId, @NotNull Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> add(@NotNull Long ownerId, ItemDto itemDto) {
        return post("", ownerId, itemDto);
    }

    public ResponseEntity<Object> update(@NotNull Long ownerId, @Valid Long itemId, @Valid ItemUpdateDto itemDto) {
        return patch("/" + itemId, ownerId, itemDto);
    }

    public ResponseEntity<Object> addComment(Long itemId, Long authorId, CommentDto comment) {
        return post("/" + itemId + "/comment", authorId, comment);
    }
}
