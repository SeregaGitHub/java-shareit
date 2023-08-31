package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    String SELECT_ITEM_REQUEST_DTO = "select new ru.practicum.shareit.request.dto.ItemRequestDto" +
                                     "(r.id, r.description, r.created, u.id) " +
                                     "from ItemRequest as r " +
                                     "JOIN r.requester as u ";

    @Query(
            value = SELECT_ITEM_REQUEST_DTO +
                    "where u.id = ?1 " +
                    "order by r.created desc"
    )
    List<ItemRequestDto> getItemRequestsOfUserList(Integer userId);

    @Query(
            value = SELECT_ITEM_REQUEST_DTO +
                    "where u.id <> ?1 " +
                    "order by r.created desc"
    )
    List<ItemRequestDto> getItemRequestsList(Integer userId);

    @Query(
            value = SELECT_ITEM_REQUEST_DTO +
                    "where u.id <> ?1 " +
                    "order by r.created desc"
    )
    List<ItemRequestDto> getItemRequestsList(Integer userId, PageRequest pageRequest);
}
