package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingAndCommentsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void saveItemTest() throws Exception {
        long ownerId = 1;

        ItemDto itemDto = createItemDto(1, "Шкаф", "Большой шкаф", true);

        when(itemService.saveItem(eq(ownerId), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).saveItem(eq(ownerId), any(ItemDto.class));
    }

    @Test
    void updateItemTest() throws Exception {
        long ownerId = 1;
        long itemId = 1;

        ItemDto newItemDto = createItemDto(itemId, "Шкаф-купе", "Огромный шкаф-купе", true);

        when(itemService.updateItem(any(ItemDto.class), eq(itemId), eq(ownerId))).thenReturn(newItemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .content(objectMapper.writeValueAsString(newItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newItemDto.getId()));

        verify(itemService, times(1)).updateItem(any(ItemDto.class), eq(itemId), eq(ownerId));
    }

    @Test
    void findItemByIdTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        ItemWithBookingAndCommentsDto itemDto = createItemWithBookingAndCommentsDto(itemId, "Шкаф-купе", "Огромный шкаф-купе", true);

        when(itemService.findItemById(itemId, userId)).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).findItemById(itemId, userId);
    }

    @Test
    void findItemsByUserTest() throws Exception {
        long userId = 1;

        ItemWithBookingAndCommentsDto itemDto1 = createItemWithBookingAndCommentsDto(1, "Шкаф-купе", "Огромный шкаф-купе", true);
        ItemWithBookingAndCommentsDto itemDto2 = createItemWithBookingAndCommentsDto(2, "Лодка", "Резиновая лодка", true);
        List<ItemWithBookingAndCommentsDto> itemsDtoList = List.of(itemDto1, itemDto2);

        when(itemService.findItemsByUser(eq(userId), anyInt(), anyInt())).thenReturn(itemsDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()));

        verify(itemService, times(1)).findItemsByUser(eq(userId), anyInt(), anyInt());
    }

    @Test
    void searchItemsTest() throws Exception {
        ItemDto itemDto1 = createItemDto(1, "Шкаф-купе", "Огромный шкаф-купе", true);
        ItemDto itemDto2 = createItemDto(2, "Лодка", "Резиновая лодка", true);
        List<ItemDto> itemsDtoList = List.of(itemDto1, itemDto2);

        String text = "Example";

        when(itemService.searchItems(eq(text), anyInt(), anyInt())).thenReturn(itemsDtoList);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto1.getId()))
                .andExpect(jsonPath("$[1].id").value(itemDto2.getId()));

        verify(itemService, times(1)).searchItems(eq(text), anyInt(), anyInt());
    }

    @Test
    void addCommentTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        CommentDto commentDto = createCommentDto(1, "Отлично!");

        when(itemService.addComment(eq(userId), any(CommentDto.class), eq(itemId))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()));

        verify(itemService, times(1)).addComment(eq(userId), any(CommentDto.class), eq(itemId));
    }

    private ItemDto createItemDto(long id, String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);

        return itemDto;
    }

    private ItemWithBookingAndCommentsDto createItemWithBookingAndCommentsDto(long id, String name, String description, Boolean available) {
        ItemWithBookingAndCommentsDto itemWithBookingAndCommentsDto = new ItemWithBookingAndCommentsDto();
        itemWithBookingAndCommentsDto.setId(id);
        itemWithBookingAndCommentsDto.setName(name);
        itemWithBookingAndCommentsDto.setDescription(description);
        itemWithBookingAndCommentsDto.setAvailable(available);

        return itemWithBookingAndCommentsDto;
    }

    private CommentDto createCommentDto(long id, String text) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(id);
        commentDto.setText(text);

        return commentDto;
    }
}
