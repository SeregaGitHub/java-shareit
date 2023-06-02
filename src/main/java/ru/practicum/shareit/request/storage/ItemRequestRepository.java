package ru.practicum.shareit.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    @Query(
            value = "select new ru.practicum.shareit.request.dto.ItemRequestDto" +
                    "(r.id, r.description, r.created, u.id) " +
                    "from ItemRequest as r " +
                    "JOIN r.requester as u " +
                    "where u.id = ?1 " +
                    "order by r.created desc"
    )
    List<ItemRequestDto> getItemRequestsList(Integer userId);
}
