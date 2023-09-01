package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
@Slf4j
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> saveItem(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен POST-запрос на добавление вещи владельцем с ID={}", userId);
        return itemClient.saveItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto,
                                             @PathVariable long itemId) {
        log.debug("Получен PATCH-запрос на обновление вещи с ID={}", itemId);
        return itemClient.updateItem(userId, itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable long itemId,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получен GET-запрос на получение вещи с ID={}", itemId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("Получен GET-запрос на получение всех вещей владельца с ID={}", userId);
        return itemClient.findItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") long userId, @RequestParam String text,
                                              @RequestParam(required = false, defaultValue = "0") @Min(0) int from,
                                              @RequestParam(required = false, defaultValue = "10") @Min(1) int size) {
        log.info("Получен GET-запрос на поиск вещи с текстом={}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody CommentDto commentDto, @PathVariable long itemId) {
        log.info("Получен POST-запрос на добавление отзыва пользователем с ID={}", userId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}
