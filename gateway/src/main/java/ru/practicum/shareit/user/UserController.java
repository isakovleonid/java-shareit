package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.IncorrectDataException;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Get all users");
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@NotNull @PathVariable Long userId) {
        log.info("Get user with id={}", userId);
        return userClient.getUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@NotNull @PathVariable("id") Long id) {
        return userClient.deleteUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        if (userDto.getName().isBlank()) {
            throw new IncorrectDataException("Name должен быть заполнен");
        }
        if (userDto.getEmail().isBlank()) {
            throw new IncorrectDataException("Email Должен быть заполнен");
        }
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        return userClient.updateUser(id, userDto);
    }
}
