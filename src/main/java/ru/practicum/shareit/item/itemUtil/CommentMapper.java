package ru.practicum.shareit.item.itemUtil;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public Comment toComment(User commentUser, Item itemForComment, CommentDto commentDto, LocalDateTime now) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .commentUser(commentUser)
                .itemForComment(itemForComment)
                .created(now)
                .build();
    }
}
