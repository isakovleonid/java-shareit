package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    // Test GET /users
    @Test
    void getUsers_ShouldCallClientAndReturnOk() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.getUsers()).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk());
    }

    // Test GET /users/{id}
    @Test
    void getUser_WithValidId_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.getUser(userId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", userId))
                .andExpect(status().isOk());
    }

    // Test DELETE /users/{id}
    @Test
    void deleteUser_ShouldCallClientAndReturnResponse() throws Exception {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.noContent().build();
        Mockito.when(userClient.deleteUser(userId)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    // Test POST /users with valid data
    @Test
    void addUser_WithValidData_ShouldReturnCreated() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();
        Mockito.when(userClient.addUser(Mockito.any(UserDto.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated());
    }

    // Test POST /users with blank name
    @Test
    void addUser_WithBlankName_ShouldThrowException() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("   "); // Blank name
        userDto.setEmail("valid@email.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    // Test POST /users with blank email
    @Test
    void addUser_WithBlankEmail_ShouldThrowException() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Valid Name");
        userDto.setEmail("   "); // Blank email

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    // Test PATCH /users/{id}
    @Test
    void updateUser_ShouldCallClientAndReturnOk() throws Exception {
        Long userId = 1L;
        UserUpdateDto updateDto = new UserUpdateDto();
        // Set updateDto properties as needed

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();
        Mockito.when(userClient.updateUser(userId, updateDto)).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());
    }
}
