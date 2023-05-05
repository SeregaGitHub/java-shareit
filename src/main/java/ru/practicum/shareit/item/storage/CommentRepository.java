package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query(
            value = "select new ru.practicum.shareit.item.dto.CommentDto" +
                    "(c.id, c.text, u.name, c.created) " +
                    "from Comment as c " +
                    "JOIN c.commentUser as u " +
                    "JOIN c.itemForComment as i " +
                    "where i.id = ?1"
    )
    List<CommentDto> getItemComment(Integer itemId);
}
