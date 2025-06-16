package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDetailDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemRequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestDtoMapper itemRequestDtoMapper;

    private final User testUser = new User(1L, "User Name", "user@mail.com");
    private final ItemRequest testRequest = new ItemRequest(
            1L,
            "Запрос дрели",
            LocalDateTime.now(),
            testUser
    );
    private final ItemRequestDto testRequestDto = new ItemRequestDto(
            testRequest.getId(),
            testRequest.getDescription(),
            testRequest.getCreated()
    );
    private final ItemRequestDetailDto testDetailDto = new ItemRequestDetailDto(
            testRequest.getId(),
            testRequest.getDescription(),
            testRequest.getCreated(),
            Collections.emptyList()
    );

    @Test
    void addRequest_shouldReturnCreated() throws Exception {
        when(itemRequestDtoMapper.fromDto(any(ItemRequestDto.class))).thenReturn(testRequest);
        when(itemRequestService.add(anyLong(), any(ItemRequest.class))).thenReturn(testRequest);
        when(itemRequestDtoMapper.toDto(any(ItemRequest.class))).thenReturn(testRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testRequestDto.getDescription())));
    }

    @Test
    void addRequest_shouldReturnBadRequestWhenMissingHeader() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOtherUserRequests_shouldReturnOk() throws Exception {
        when(itemRequestService.getAllOtherUserRequest(anyLong()))
                .thenReturn(List.of(testRequest));
        when(itemRequestDtoMapper.toDto(any(ItemRequest.class)))
                .thenReturn(testRequestDto);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testRequestDto.getId()), Long.class));
    }

    @Test
    void getRequestById_shouldReturnOk() throws Exception {
        when(itemRequestService.getDetailedRequestById(anyLong()))
                .thenReturn(testDetailDto);

        mockMvc.perform(get("/requests/{id}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testDetailDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(testDetailDto.getDescription())));
    }

    @Test
    void getAllUserRequests_shouldReturnOk() throws Exception {
        when(itemRequestService.getDetailedRequestsAll(anyLong()))
                .thenReturn(List.of(testDetailDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testDetailDto.getId()), Long.class));
    }

    @Test
    void getAllUserRequests_shouldReturnEmptyList() throws Exception {
        when(itemRequestService.getDetailedRequestsAll(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }
}