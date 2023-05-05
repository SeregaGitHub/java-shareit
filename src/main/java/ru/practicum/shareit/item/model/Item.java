package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@Builder
public class Item {
    private Integer id;
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotBlank
    @Size(max = 1024)
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    private Boolean available;
    @NotNull
    @Positive
    private User owner;
}
