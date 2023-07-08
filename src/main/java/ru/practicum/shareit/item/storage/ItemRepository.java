package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemShot;
import ru.practicum.shareit.item.dto.ItemWithRequestIdDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(String name, String description);

    List<Item> findByAvailableTrueAndNameIgnoreCaseOrAvailableTrueAndDescriptionIgnoreCaseContaining(String name, String description, PageRequest pageRequest);

    List<ItemShot> findItemsShotByOwner_Id(Integer id);

    List<ItemShot> findItemsShotByOwner_Id(Integer id, PageRequest pageRequest);

    @Query(
            value = "select i " +
                    "from Item as i " +
                    "JOIN FETCH i.owner as o " +
                    "where i.id = ?1")
    Optional<Item> findItemByIdWithOwner(Integer id);

    @Query(
            value = "select new ru.practicum.shareit.item.dto.ItemWithRequestIdDto" +
                    "(i.id, i.name, i.description, i.available, r.id) " +
                    "from Item as i " +
                    "JOIN i.itemRequest as r " +
                    "where i.available = true " +
                    "and r.id in (?1) " +
                    "order by r.id desc"
    )
    List<ItemWithRequestIdDto> getItemsWithRequestDtoList(Set<Integer> requestsId);
}

