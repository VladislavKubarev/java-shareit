package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemResponseDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return ItemRequestMapper.toItemResponseDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemResponseDto> findRequestsByRequester(long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        List<ItemRequest> itemRequestsList = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(userId);

        List<ItemResponseDto> itemResponseList = itemRequestsList.stream().map(ItemRequestMapper::toItemResponseDto).collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findByRequestIn(itemRequestsList)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        for (ItemResponseDto itemResponseDto : itemResponseList) {
            List<Item> itemsList = itemsByRequest.getOrDefault(itemResponseDto.getId(), Collections.emptyList());
            itemResponseDto.setItems(itemsList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        }

        return itemResponseList;
    }

    @Override
    public List<ItemResponseDto> findAllRequests(long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        Pageable pageable = PageRequest.of(from / size, size);

        List<ItemRequest> itemRequestsList = itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(userId, pageable);

        List<ItemResponseDto> itemResponseList = itemRequestsList.stream().map(ItemRequestMapper::toItemResponseDto).collect(Collectors.toList());

        Map<Long, List<Item>> itemsByRequest = itemRepository.findByRequestIn(itemRequestsList)
                .stream().collect(Collectors.groupingBy(item -> item.getRequest().getId(), Collectors.toList()));

        for (ItemResponseDto itemResponseDto : itemResponseList) {
            List<Item> itemsList = itemsByRequest.getOrDefault(itemResponseDto.getId(), Collections.emptyList());
            itemResponseDto.setItems(itemsList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
        }

        return itemResponseList;
    }

    @Override
    public ItemResponseDto findRequestById(long userId, long requestId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден!"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос вещи не найден!"));
        ItemResponseDto itemResponseDto = ItemRequestMapper.toItemResponseDto(itemRequest);

        List<Item> itemsList = itemRepository.findByRequestId(itemRequest.getId());
        List<ItemDto> itemsDtoList = itemsList.stream()
                .map(ItemMapper::toItemDto).collect(toList());

        itemResponseDto.setItems(itemsDtoList);

        return itemResponseDto;
    }
}
