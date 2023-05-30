package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemShot;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(String name, String description);

    List<ItemShot> findItemsShotByOwner_Id(Integer id);

    @Query(
            "select i " +
            "from Item as i " +
            "JOIN FETCH i.owner as o " +
            "where i.id = ?1")
    Optional<Item> findItemByIdWithOwner(Integer id);
}

