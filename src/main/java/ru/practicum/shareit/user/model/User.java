package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Integer id;
    @NotNull
    @NotBlank
    @Size(max = 64)
    private String name;
    @NotNull
    @Email
    private String email;
}
