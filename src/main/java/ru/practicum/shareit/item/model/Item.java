package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
public class Item {
    private Integer id;
    @NotNull
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotNull
    @NotBlank
    @Size(max = 1024)
    private String description;
    @NotNull
    @JsonProperty(value = "available")
    private Boolean available;
    @NotNull
    private Integer owner;
}
