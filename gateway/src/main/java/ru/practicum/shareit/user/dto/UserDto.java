package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotBlank
    @NotNull
    private String name;
    @Email(message = "Адрес электронной почты должен соответствовать формату email")
    @NotNull
    @NotBlank
    private String email;
}
