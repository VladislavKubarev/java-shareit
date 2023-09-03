package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemResponseDto createRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemResponseDto> findRequestsByRequester(long userId);

    List<ItemResponseDto> findAllRequests(long userId, int from, int size);

    ItemResponseDto findRequestById(long userId, long requestId);
}
