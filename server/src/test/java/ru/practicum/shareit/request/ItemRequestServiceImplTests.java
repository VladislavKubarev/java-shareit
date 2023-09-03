package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTests {

    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestServiceImpl;

    @Test
    void createValidRequestTest() {
        User user = createUser(1, "Владислав", "vlad@yandex.ru");

        ItemRequest itemRequest = createItemRequest(1, "Хочу сделать запрос", user, LocalDateTime.now());
        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemResponseDto actualItemRequest = itemRequestServiceImpl.createRequest(user.getId(), itemRequestDto);

        assertThat(actualItemRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(actualItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(actualItemRequest.getCreated(), equalTo(itemRequest.getCreated()));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void createRequestWhenUserIsNotFoundTest() {
        long userId = 0;

        ItemRequestDto itemRequestDto = new ItemRequestDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.createRequest(userId, itemRequestDto));

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void findRequestsByRequesterTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User requester = createUser(2, "Иван", "ivan@yandex.ru");

        ItemRequest itemRequest1 = createItemRequest(1, "Хочу сделать запрос", requester, LocalDateTime.now());
        ItemRequest itemRequest2 = createItemRequest(2, "Хочу сделать еще запрос", requester, LocalDateTime.now().plusHours(1));

        Item item1 = createItem(1, "Шкаф", "Большой шкаф", true, owner, itemRequest1);
        Item item2 = createItem(2, "Лодка", "Дырявая резиновая лодка", true, owner, itemRequest2);

        List<ItemRequest> itemRequestsList = List.of(itemRequest1, itemRequest2);
        List<Item> itemsList = List.of(item1, item2);

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requester.getId())).thenReturn(itemRequestsList);
        when(itemRepository.findByRequestIn(itemRequestsList)).thenReturn(itemsList);

        List<ItemResponseDto> actualItemRequests = itemRequestServiceImpl.findRequestsByRequester(requester.getId());

        assertThat(actualItemRequests.get(0).getId(), equalTo(itemRequestsList.get(0).getId()));
        assertThat(actualItemRequests.get(0).getDescription(), equalTo(itemRequestsList.get(0).getDescription()));
        assertThat(actualItemRequests.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(actualItemRequests.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(actualItemRequests.get(0).getItems().size(), equalTo(1));

        assertThat(actualItemRequests.get(1).getId(), equalTo(itemRequestsList.get(1).getId()));
        assertThat(actualItemRequests.get(1).getDescription(), equalTo(itemRequestsList.get(1).getDescription()));
        assertThat(actualItemRequests.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(actualItemRequests.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(actualItemRequests.get(1).getItems().size(), equalTo(1));

        assertThat(actualItemRequests.size(), equalTo(2));

        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRequestRepository, times(1)).findByRequesterIdOrderByCreatedDesc(requester.getId());
        verify(itemRepository, times(1)).findByRequestIn(itemRequestsList);
    }

    @Test
    void findAllRequestsTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User requester = createUser(2, "Иван", "ivan@yandex.ru");

        ItemRequest itemRequest1 = createItemRequest(1, "Хочу сделать запрос", requester, LocalDateTime.now());
        ItemRequest itemRequest2 = createItemRequest(2, "Хочу сделать еще запрос", requester, LocalDateTime.now().plusHours(1));

        Item item1 = createItem(1, "Шкаф", "Большой шкаф", true, owner, itemRequest1);
        Item item2 = createItem(2, "Лодка", "Дырявая резиновая лодка", true, owner, itemRequest2);

        List<ItemRequest> itemRequestsList = List.of(itemRequest1, itemRequest2);
        List<Item> itemsList = List.of(item1, item2);

        when(userRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findByRequesterIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(itemRequestsList);
        when(itemRepository.findByRequestIn(itemRequestsList)).thenReturn(itemsList);

        List<ItemResponseDto> actualItemRequests = itemRequestServiceImpl.findAllRequests(owner.getId(), 0, 5);

        assertThat(actualItemRequests.get(0).getId(), equalTo(itemRequestsList.get(0).getId()));
        assertThat(actualItemRequests.get(0).getDescription(), equalTo(itemRequestsList.get(0).getDescription()));
        assertThat(actualItemRequests.get(0).getItems().get(0).getId(), equalTo(item1.getId()));
        assertThat(actualItemRequests.get(0).getItems().get(0).getName(), equalTo(item1.getName()));
        assertThat(actualItemRequests.get(0).getItems().size(), equalTo(1));

        assertThat(actualItemRequests.get(1).getId(), equalTo(itemRequestsList.get(1).getId()));
        assertThat(actualItemRequests.get(1).getDescription(), equalTo(itemRequestsList.get(1).getDescription()));
        assertThat(actualItemRequests.get(1).getItems().get(0).getId(), equalTo(item2.getId()));
        assertThat(actualItemRequests.get(1).getItems().get(0).getName(), equalTo(item2.getName()));
        assertThat(actualItemRequests.get(1).getItems().size(), equalTo(1));

        assertThat(actualItemRequests.size(), equalTo(2));

        verify(userRepository, times(1)).findById(owner.getId());
        verify(itemRequestRepository, times(1)).findByRequesterIdNotOrderByCreatedDesc(eq(owner.getId()), any(Pageable.class));
        verify(itemRepository, times(1)).findByRequestIn(itemRequestsList);
    }

    @Test
    void findRequestsByRequesterWhenRequesterIsNotFoundTest() {
        long requesterId = 0;

        when(userRepository.findById(requesterId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findRequestsByRequester(requesterId));

        verify(userRepository, times(1)).findById(requesterId);
    }

    @Test
    void findAllRequestsWhenUserIsNotFoundTest() {
        long userId = 0;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findAllRequests(userId, 0, 5));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findRequestByIdTest() {
        User owner = createUser(1, "Владислав", "vlad@yandex.ru");
        User requester = createUser(2, "Иван", "ivan@yandex.ru");

        ItemRequest itemRequest = createItemRequest(1, "Хочу сделать запрос", requester, LocalDateTime.now());

        Item item = createItem(1, "Шкаф", "Большой шкаф", true, owner, itemRequest);

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(itemRequest.getId())).thenReturn(List.of(item));

        ItemResponseDto actualItemRequests = itemRequestServiceImpl.findRequestById(requester.getId(), itemRequest.getId());

        assertThat(actualItemRequests.getId(), equalTo(actualItemRequests.getId()));
        assertThat(actualItemRequests.getDescription(), equalTo(actualItemRequests.getDescription()));
        assertThat(actualItemRequests.getItems().get(0).getId(), equalTo(item.getId()));
        assertThat(actualItemRequests.getItems().get(0).getName(), equalTo(item.getName()));
        assertThat(actualItemRequests.getItems().size(), equalTo(1));

        verify(userRepository, times(1)).findById(requester.getId());
        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
        verify(itemRepository, times(1)).findByRequestId(itemRequest.getId());
    }

    @Test
    void findRequestByIdWhenUserIsNotFoundTest() {
        long userId = 0;
        long requestId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findRequestById(userId, requestId));

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findRequestByIdWhenRequestIsNotFoundTest() {
        User user = createUser(1, "Владислав", "vlad@yandex.ru");
        long requestId = 0;

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestServiceImpl.findRequestById(user.getId(), requestId));

        verify(userRepository, times(1)).findById(user.getId());
        verify(itemRequestRepository, times(1)).findById(requestId);
    }


    private User createUser(long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private ItemRequest createItemRequest(long id, String description, User requester, LocalDateTime created) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(id);
        itemRequest.setDescription(description);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(created);

        return itemRequest;
    }

    private Item createItem(long id, String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequest(request);

        return item;
    }
}
