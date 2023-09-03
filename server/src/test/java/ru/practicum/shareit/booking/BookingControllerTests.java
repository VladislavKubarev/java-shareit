package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void createBookingTest() throws Exception {
        long userId = 1;
        long itemId = 1;

        BookingRequestDto bookingRequestDto = createBookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5), itemId);

        BookingResponseDto bookingResponseDto = createBookingResponseDto(1);

        when(bookingService.createBooking(eq(userId), any(BookingRequestDto.class))).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart()))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()));

        verify(bookingService, times(1)).createBooking(eq(userId), any(BookingRequestDto.class));
    }

    @Test
    void approveBookingTest() throws Exception {
        long userId = 1;

        BookingResponseDto bookingResponseDto = createBookingResponseDto(1);

        when(bookingService.approveBooking(eq(userId), eq(bookingResponseDto.getId()), anyBoolean())).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{id}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart()))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()));

        verify(bookingService, times(1)).approveBooking(userId, bookingResponseDto.getId(), true);
    }

    @Test
    void findBookingByIdTest() throws Exception {
        long userId = 1;

        BookingResponseDto bookingResponseDto = createBookingResponseDto(1);

        when(bookingService.findBookingById(userId, bookingResponseDto.getId())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{id}", bookingResponseDto.getId())
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingResponseDto.getStart()))
                .andExpect(jsonPath("$.end").value(bookingResponseDto.getEnd()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.booker").value(bookingResponseDto.getBooker()))
                .andExpect(jsonPath("$.item").value(bookingResponseDto.getItem()));

        verify(bookingService, times(1)).findBookingById(userId, bookingResponseDto.getId());
    }

    @Test
    void findBookingsByBookerTest() throws Exception {
        long userId = 1;

        List<BookingResponseDto> bookingList = List.of(createBookingResponseDto(1), createBookingResponseDto(2));

        when(bookingService.findBookingsByBooker(eq(userId), any(), anyInt(), anyInt())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingList.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookingList.get(0).getStart()))
                .andExpect(jsonPath("$[0].end").value(bookingList.get(0).getEnd()))
                .andExpect(jsonPath("$[0].status").value(bookingList.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].booker").value(bookingList.get(0).getBooker()))
                .andExpect(jsonPath("$[0].item").value(bookingList.get(0).getItem()))
                .andExpect(jsonPath("$[1].id").value(bookingList.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(bookingList.get(1).getStart()))
                .andExpect(jsonPath("$[1].end").value(bookingList.get(1).getEnd()))
                .andExpect(jsonPath("$[1].status").value(bookingList.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].booker").value(bookingList.get(1).getBooker()))
                .andExpect(jsonPath("$[1].item").value(bookingList.get(1).getItem()));

        verify(bookingService, times(1)).findBookingsByBooker(eq(userId), eq("ALL"), anyInt(), anyInt());
    }

    @Test
    void findBookingsByOwnerTest() throws Exception {
        long ownerId = 1;

        List<BookingResponseDto> bookingList = List.of(createBookingResponseDto(1), createBookingResponseDto(2));

        when(bookingService.findBookingsByOwner(eq(ownerId), any(), anyInt(), anyInt())).thenReturn(bookingList);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingList.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookingList.get(0).getStart()))
                .andExpect(jsonPath("$[0].end").value(bookingList.get(0).getEnd()))
                .andExpect(jsonPath("$[0].status").value(bookingList.get(0).getStatus().toString()))
                .andExpect(jsonPath("$[0].booker").value(bookingList.get(0).getBooker()))
                .andExpect(jsonPath("$[0].item").value(bookingList.get(0).getItem()))
                .andExpect(jsonPath("$[1].id").value(bookingList.get(1).getId()))
                .andExpect(jsonPath("$[1].start").value(bookingList.get(1).getStart()))
                .andExpect(jsonPath("$[1].end").value(bookingList.get(1).getEnd()))
                .andExpect(jsonPath("$[1].status").value(bookingList.get(1).getStatus().toString()))
                .andExpect(jsonPath("$[1].booker").value(bookingList.get(1).getBooker()))
                .andExpect(jsonPath("$[1].item").value(bookingList.get(1).getItem()));

        verify(bookingService, times(1)).findBookingsByOwner(eq(ownerId), eq("ALL"), anyInt(), anyInt());
    }

    private BookingRequestDto createBookingRequestDto(LocalDateTime start, LocalDateTime end, long itemId) {
        BookingRequestDto bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setStart(start);
        bookingRequestDto.setEnd(end);
        bookingRequestDto.setItemId(itemId);

        return bookingRequestDto;
    }

    private BookingResponseDto createBookingResponseDto(long id) {
        BookingResponseDto bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(id);
        bookingResponseDto.setStatus(BookingStatus.WAITING);

        return bookingResponseDto;
    }
}
