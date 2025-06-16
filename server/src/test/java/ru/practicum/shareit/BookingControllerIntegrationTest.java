package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.dto.BookingAddDtoMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private BookingDtoMapper bookingDtoMapper;

    @MockBean
    private BookingAddDtoMapper bookingAddDtoMapper;

    private final User testUser = new User(1L, "Тестовый пользователь", "test@mail.com");

    private final User testOwner = new User(2L, "Тестовый владелец", "owner@mail.com");

    private final Item testItem = new Item(1L, "Предмет", "Описание предмета", true, testOwner, null);

    private final Booking testBooking = new Booking(
            1L,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            testItem,
            testUser,
            BookingStatus.WAITING
            );

    private final BookingDto testBookingDto = new BookingDto(testBooking.getId(),
            testBooking.getStart(),
            testBooking.getEnd(),
            testBooking.getItem(),
            testBooking.getBooker(),
            testBooking.getStatus());

    private final BookingAddDto testBookingAddDto = new BookingAddDto(LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            1L);

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(testBooking);
        when(bookingDtoMapper.toDTO(any(Booking.class))).thenReturn(testBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void getBookingsByState_shouldReturnList() throws Exception {
        when(bookingService.getByBookerId(anyLong(), any()))
                .thenReturn(List.of(testBooking));
        when(bookingDtoMapper.toDTO(any(Booking.class))).thenReturn(testBookingDto);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void addBooking_shouldCreateBooking() throws Exception {
        when(bookingAddDtoMapper.fromDTO(any(BookingAddDto.class))).thenReturn(testBooking);
        when(bookingService.add(any(Booking.class), anyLong(), anyLong())).thenReturn(testBooking);
        when(bookingDtoMapper.toDTO(any(Booking.class))).thenReturn(testBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void addBooking_shouldReturnBadRequestForMissingHeader() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking_shouldUpdateStatus() throws Exception {
        Booking approvedBooking = testBooking.toBuilder()
                .status(BookingStatus.APPROVED)
                .build();

        BookingDto approvedDto = new BookingDto();
        approvedDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(anyLong(), anyLong(), anyBoolean())).thenReturn(approvedBooking);
        when(bookingDtoMapper.toDTO(any(Booking.class))).thenReturn(approvedDto);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
    }
}