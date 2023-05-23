package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "item", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "item_name", length = 64, nullable = false)
    @NotBlank
    @Size(max = 64)
    private String name;
    @Column(name = "item_description", length = 1024, nullable = false)
    @NotBlank
    @Size(max = 1024)
    private String description;
    @Column(name = "item_available", nullable = false)
    @NotNull
    @JsonProperty(value = "available")
    private Boolean available;
    @NotNull
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;
}
