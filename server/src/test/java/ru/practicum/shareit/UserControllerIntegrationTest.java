package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = User.builder()
                .name("Тестовый пользователь")
                .email("test@example.com")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(testUser.getName())))
                .andExpect(jsonPath("$[0].email", is(testUser.getEmail())));
    }

    @Test
    void getUserById_shouldReturnUser_whenUserExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testUser.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));
    }

    @Test
    void getUserById_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        long nonExistentId = 99999999L;
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addUser_shouldCreateNewUser() throws Exception {
        UserDto newUser = new UserDto();
        newUser.setName("Новые пользователь");
        newUser.setEmail("new@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(newUser.getName())))
                .andExpect(jsonPath("$.email", is(newUser.getEmail())))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    void addUser_shouldReturnBadRequest_whenEmailIsInvalid() throws Exception {
        UserDto invalidUser = new UserDto();
        invalidUser.setName("Новое имя");
        invalidUser.setEmail("invalid-email");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUser_shouldReturnBadRequest_whenNameIsBlank() throws Exception {
        UserDto invalidUser = new UserDto();
        invalidUser.setName("");
        invalidUser.setEmail("valid@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() throws Exception {
        UserDto updatedUser = new UserDto();
        updatedUser.setName("UНовое имя");
        updatedUser.setEmail("updated@example.com");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        long nonExistentId = 99999999L;
        UserDto updatedUser = new UserDto();
        updatedUser.setName("Новое имя");
        updatedUser.setEmail("updated@example.com");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_shouldUpdateOnlyName_whenEmailIsNull() throws Exception {
        UserDto partialUpdate = new UserDto();
        partialUpdate.setName("Не указан email");

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is(partialUpdate.getName())))
                .andExpect(jsonPath("$.email", is(testUser.getEmail())));
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}