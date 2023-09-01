package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;


@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен POST-запрос на создание запроса на вещь от пользователя с ID={}", userId);
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findRequestsByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET-запрос на получение списка запросов на вещи пользователя с ID={}", userId);
        return itemRequestClient.findRequestsByRequester(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("Получен GET-запрос на получение списка всех запросов на вещи от пользователя с ID={}", userId);
        return itemRequestClient.findAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable long requestId) {
        log.info("Получен GET-запрос на получение запроса на вещь от пользователя с ID={}", userId);
        return itemRequestClient.findRequestById(userId, requestId);
    }
}
