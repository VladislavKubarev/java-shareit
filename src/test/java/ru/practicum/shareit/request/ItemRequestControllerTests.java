package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createRequestTest() throws Exception {
        long userId = 1;

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Хочу взять вещь");

        ItemResponseDto itemResponseDto = createItemResponseDto(1, "Хочу взять вещь", LocalDateTime.now());

        when(itemRequestService.createRequest(eq(userId), any(ItemRequestDto.class))).thenReturn(itemResponseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()));

        verify(itemRequestService, times(1)).createRequest(eq(userId), any(ItemRequestDto.class));
    }

    @Test
    void findRequestsByRequesterTest() throws Exception {
        long userId = 1;

        List<ItemResponseDto> itemResponseList = List.of(
                createItemResponseDto(1, "Хочу взять вещь", LocalDateTime.now()),
                createItemResponseDto(2, "Еще хочу взять эту вещь", LocalDateTime.now().plusDays(1)));

        when(itemRequestService.findRequestsByRequester(userId)).thenReturn(itemResponseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value((itemResponseList.get(1).getId())));

        verify(itemRequestService, times(1)).findRequestsByRequester(userId);
    }

    @Test
    void findAllRequestsTest() throws Exception {
        long userId = 1;

        List<ItemResponseDto> itemResponseList = List.of(
                createItemResponseDto(1, "Хочу взять вещь", LocalDateTime.now()),
                createItemResponseDto(2, "Еще хочу взять эту вещь", LocalDateTime.now().plusDays(1)));

        when(itemRequestService.findAllRequests(eq(userId), anyInt(), anyInt())).thenReturn(itemResponseList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value((itemResponseList.get(1).getId())));

        verify(itemRequestService, times(1)).findAllRequests(eq(userId), anyInt(), anyInt());
    }

    @Test
    void findRequestByIdTest() throws Exception {
        long userId = 1;

        ItemResponseDto itemResponseDto = createItemResponseDto(1, "Хочу взять вещь", LocalDateTime.now());

        when(itemRequestService.findRequestById(userId, itemResponseDto.getId())).thenReturn(itemResponseDto);

        mockMvc.perform(get("/requests/{id}", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()));

        verify(itemRequestService, times(1)).findRequestById(userId, itemResponseDto.getId());
    }

    private ItemResponseDto createItemResponseDto(long id, String description, LocalDateTime created) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(id);
        itemResponseDto.setDescription(description);
        itemResponseDto.setCreated(created);

        return itemResponseDto;
    }
}
