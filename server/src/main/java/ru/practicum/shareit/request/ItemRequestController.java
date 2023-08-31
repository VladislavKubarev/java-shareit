package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemResponseDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemResponseDto> findRequestsByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.findRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public List<ItemResponseDto> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        return itemRequestService.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @PathVariable long requestId) {
        return itemRequestService.findRequestById(userId, requestId);
    }
}
