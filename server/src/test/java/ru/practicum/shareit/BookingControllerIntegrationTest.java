package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User testOwner;
    private Booking testBooking;
    private Item testItem;

    private BookingAddDto testBookingAddDto;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .name("Тестовый пользователь")
                .email("test11111@example.com")
                .build();
        testUser = userRepository.save(testUser);

        testOwner = User.builder()
                .name("Тестовый владелец")
                .email("owner11111@mail.com")
                .build();
        testOwner = userRepository.save(testOwner);

        testItem = Item.builder()
                .name("Предмет")
                .description("Описание")
                .owner(testOwner)
                .available(true).build();

        testItem = itemRepository.save(testItem);

        testBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(testItem)
                .booker(testUser)
                .status(BookingStatus.from("WAITING"))
                .build();

        testBooking = bookingRepository.save(testBooking);

        testBookingAddDto = new BookingAddDto(LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(3),
                testItem.getId());
    }

    @AfterEach
    void deleteRepository() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getBookingById_shouldReturnBooking() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookings/{id}", testBooking.getId())
                                .header("X-Sharer-User-Id", testUser.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testBooking.getId().intValue())))
                .andExpect(jsonPath("$.status", is(BookingStatus.WAITING.toString())));
    }

    @Test
    void getBookingsByState_shouldReturnList() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testBooking.getId().intValue())));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "CURRENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "FUTURE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testBooking.getId().intValue())));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(testBooking.getId().intValue())));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", BookingStatus.from("REJECTED").toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("state", "ERRORCODE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void getBookingById_notAllowedRequestOtherUserBooking() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/bookings/{id}", testBooking.getId())
                                .header("X-Sharer-User-Id", 9999999999L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void addBooking_shouldCreateBooking() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("WAITING")));
    }

    @Test
    void addBooking_shouldReturnBadRequestForMissingHeader() throws Exception {
        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addBooking_notAllowedBookByOwner() throws Exception {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", testOwner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testBookingAddDto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void addBooking_NotAllowedStartDateMoreEndDate() throws Exception {
        BookingAddDto newBooking = new BookingAddDto(LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(2),
                testItem.getId());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newBooking)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void approveBooking_UpdateStatus() throws Exception {
        mockMvc.perform(patch("/bookings/{id}",testBooking.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(BookingStatus.from("APPROVED").toString())));

        mockMvc.perform(patch("/bookings/{id}",testBooking.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(patch("/bookings/{id}",testBooking.getId())
                        .header("X-Sharer-User-Id", 9999999999L)
                        .param("approved", "true"))
                .andExpect(status().is5xxServerError());

        mockMvc.perform(patch("/bookings/{id}",testBooking.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));

        mockMvc.perform(patch("/bookings/{id}",testBooking.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .param("approved", "false"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void addComment_UnallowedCommentNotEndBoooking() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                        .text("Тестовый комментарий")
                        .created(LocalDateTime.now())
                        .build();

        mockMvc.perform(post("/items/{id}/comment", testItem.getId())
                .header("X-Sharer-User-Id", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void addComment_AllowedCommentEndBoooking() throws Exception {
        Booking newBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(testItem)
                .booker(testUser)
                .status(BookingStatus.from("APPROVED"))
                .build();

        bookingRepository.save(newBooking);

        CommentDto commentDto = CommentDto.builder()
                .text("Тестовый комментарий")
                .created(LocalDateTime.now())
                .build();

        mockMvc.perform(post("/items/{id}/comment", testItem.getId())
                        .header("X-Sharer-User-Id", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}