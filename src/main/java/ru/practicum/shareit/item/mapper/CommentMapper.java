package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public Comment toComment(CommentDto commentDto, User author, Item item, LocalDateTime created) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(created);

        return comment;
    }
}
