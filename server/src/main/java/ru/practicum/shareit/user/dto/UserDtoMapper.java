package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.mapper.DTOMapper;
import ru.practicum.shareit.user.User;

@Component
public class UserDtoMapper implements DTOMapper<UserDto, User> {

    @Override
    public UserDto toDTO(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    @Override
    public User fromDTO(UserDto userDto) {
        User user = User.builder().build();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }
}
