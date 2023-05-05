package ru.practicum.shareit.item.model;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(User commentUser, Item itemForComment , CommentDto commentDto, LocalDateTime now) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .commentUser(commentUser)
                .itemForComment(itemForComment)
                .created(now)
                .build();
    }
}
