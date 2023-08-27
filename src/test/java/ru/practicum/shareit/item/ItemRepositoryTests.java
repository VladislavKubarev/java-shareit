package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class ItemRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    void searchItems() {
        Pageable pageable = PageRequest.of(0, 10);

        User owner = createUser("Влад", "vlad@yandex.ru");
        entityManager.persist(owner);

        Item item1 = createItem("Гаечный ключ", "Большой металлический ключ", true, owner);
        entityManager.persist(item1);
        Item item2 = createItem("Ключ от всех дверей", "Откроет любую дверь", true, owner);
        entityManager.persist(item2);
        Item item3 = createItem("Скрипичный ключ", "Может кому пригодится", false, owner);
        entityManager.persist(item3);
        Item item4 = createItem("Диван", "Старый и сломанный", true, owner);
        entityManager.persist(item4);

        assertThat(itemRepository.searchItems("Отвертка", pageable)).size().isEqualTo(0);
        assertThat(itemRepository.searchItems("кЛюЧ", pageable)).size().isEqualTo(2);
        assertThat(itemRepository.searchItems("дИваН", pageable)).size().isEqualTo(1);
    }

    private User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);

        return user;
    }

    private Item createItem(String name, String description, Boolean available, User owner) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);

        return item;
    }
}
