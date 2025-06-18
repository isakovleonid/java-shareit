package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestDtoMapper itemRequestDtoMapper;

    @Autowired
    private ItemDtoMapper itemDtoMapper;

    private User testUser;
    private ItemRequest testRequest;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Тестовый пользователь")
                .email("test11111@example.com")
                .build();
        testUser = userRepository.save(testUser);

        testRequest = new ItemRequest(
                1L,
                "Запрос дрели",
                LocalDateTime.now(),
                testUser
        );

        testRequest = itemRequestRepository.save(testRequest);
    }

    @AfterEach
    void deleteRepository() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addRequest_shouldReturnCreated() throws Exception {
        ItemRequestDto testRequestDto = new ItemRequestDto(
                testRequest.getId(),
                testRequest.getDescription(),
                testRequest.getCreated());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isOk());

        User owner = userRepository.save(
            new User(null, "Тестовый пользователь", "test@abc.com")
        );

        Item testItem = itemRepository.save(
                new Item(null, "Дрель", "Мощная дрель", true, owner, testRequest)
        );

        ItemDto itemDto = itemDtoMapper.toDTO(testItem);

        mockMvc.perform(patch("/items/{id}", testItem.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addRequest_shouldReturnBadRequestWhenMissingHeader() throws Exception {
        ItemRequestDto testRequestDto = new ItemRequestDto(
                testRequest.getId(),
                testRequest.getDescription(),
                testRequest.getCreated());

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRequest_shouldReturnNullUser() throws Exception {
        ItemRequestDto testRequestDto = new ItemRequestDto(
                testRequest.getId(),
                testRequest.getDescription(),
                testRequest.getCreated());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id","")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addRequest_shouldReturnNullDesc() throws Exception {
        ItemRequestDto testRequestDto = new ItemRequestDto(
                testRequest.getId(),
                null,
                testRequest.getCreated());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id",testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void getAllOtherUserRequests_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 99999999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRequest.getId()), Long.class));
    }

    @Test
    void getRequestById_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests/{id}", testRequest.getId())
                        .header("X-Sharer-User-Id", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testRequest.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testRequest.getDescription())));
    }

    @Test
    void getAllUserRequests_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRequest.getId()), Long.class));
    }

    @Test
    void getAllUserRequests_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 9999999L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}