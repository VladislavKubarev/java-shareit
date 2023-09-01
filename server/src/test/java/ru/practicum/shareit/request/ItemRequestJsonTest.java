package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> requestJacksonTester;

    @Autowired
    private JacksonTester<ItemResponseDto> responseJacksonTester;

    @Test
    void itemRequestDtoTest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto("Ищу девушку");

        JsonContent<ItemRequestDto> result = requestJacksonTester.write(itemRequestDto);

        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemRequestDto.getDescription());
    }

    @Test
    void itemResponseDtoTest() throws Exception {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1);
        itemResponseDto.setDescription("Ищу девушку");
        itemResponseDto.setCreated(LocalDateTime.of(2023, 2, 23, 17, 20));
        itemResponseDto.setItems(new ArrayList<>());

        JsonContent<ItemResponseDto> result = responseJacksonTester.write(itemResponseDto);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.created");
        assertThat(result).hasJsonPath("$.items");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) itemResponseDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemResponseDto.getDescription());
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(itemResponseDto.getCreated().toString() + ":00");
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(itemResponseDto.getItems());
    }
}
