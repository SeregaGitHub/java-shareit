package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.mark.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    Integer id;
    @NotBlank(groups = {Create.class}, message = "Комментарий не может быть пустым.")
    @Size(max = 3000, groups = {Create.class}, message = "Комментарий не может быть больше 3000 символов.")
    String text;
    String authorName;
    LocalDateTime created = LocalDateTime.now();
}
