package ru.practicum.shareit.user;

import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll().stream()
                .map(userDtoMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long id) {
        return userDtoMapper.toDTO(userService.getById(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        userService.delete(id);
    }

    @PostMapping
    public UserDto add(@Valid @RequestBody UserDto userDto) {
        User user = userDtoMapper.fromDTO(userDto);
        return userDtoMapper.toDTO(userService.add(user));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        User user = userDtoMapper.fromDTO(userDto);
        user.setId(id);
        return userDtoMapper.toDTO(userService.update(user));
    }
}
