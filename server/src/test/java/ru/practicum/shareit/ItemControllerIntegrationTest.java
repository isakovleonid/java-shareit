package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.item.ItemRepository;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ItemControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // Clear repositories
        itemRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        owner = userRepository.save(new User(null, "Owner", "owner@test.com"));
        booker = userRepository.save(new User(null, "Booker", "booker@test.com"));

        // Create test item
        item = itemRepository.save(
                new Item(null, "Дрель", "Мощная дрель", true, owner, null)
        );
    }

    @Test
    void shouldCreateItem() throws Exception {
        ItemDto newItem = new ItemDto(null, "Молоток", "Тяжелый молоток", true, null, null, null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Молоток")))
                .andExpect(jsonPath("$.description", is("Тяжелый молоток")));
    }

    @Test
    void shouldGetItemById() throws Exception {
        mockMvc.perform(get("/items/{id}", item.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Дрель")));
    }

    @Test
    void shouldGetAllItemsByOwner() throws Exception {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is("Дрель")));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        ItemDto update = new ItemDto(null, "Новая дрель", null, null, null, null, null, null);

        mockMvc.perform(patch("/items/{id}", item.getId())
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Новая дрель")))
                .andExpect(jsonPath("$.description", is("Мощная дрель")));
    }

    @Test
    void shouldSearchItems() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("text", "Дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId().intValue())));
    }

    @Test
    void shouldReturnNotFoundForInvalidItemId() throws Exception {
        long nonExistentId = 99999999L;

        mockMvc.perform(get("/items/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateItemCreation() throws Exception {
        ItemDto invalidItem = new ItemDto(null, "", "", null, null, null, null, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }
}