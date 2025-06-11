package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDetailDto;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequestorIdNotOrderByCreatedDesc(Long requestorId);

    @Query("SELECT new ru.practicum.shareit.request.dto.ItemRequestDetailDto(r.id, r.description, r.created) " +
            "FROM ItemRequest r WHERE r.id = :requestId")
    Optional<ItemRequestDetailDto> findRequestById(@Param("requestId") Long requestId);

    @Query("SELECT new ru.practicum.shareit.item.dto.ItemForRequestDto(i.id, i.name, i.owner.id) " +
            "FROM Item i WHERE i.request.id = :requestId")
    List<ItemForRequestDto> findItemsForRequest(@Param("requestId") Long requestId);

    default ItemRequestDetailDto findRequestWithItems(Long requestId) {
        Optional<ItemRequestDetailDto> requestOpt = findRequestById(requestId);
        if (requestOpt.isPresent()) {
            ItemRequestDetailDto itemRequestDetailDto = requestOpt.get();
            List<ItemForRequestDto> items = findItemsForRequest(requestId);
            itemRequestDetailDto.setItems(items);
            return itemRequestDetailDto;
        }
        return null;
    }

    @Query("SELECT new ru.practicum.shareit.request.dto.ItemRequestDetailDto(r.id, r.description, r.created) " +
            "FROM ItemRequest r where (:userId is null or r.requestor.id = :userId)")
    List<ItemRequestDetailDto> findAllRequestsWithoutItems(@Param("userId") Long userId );

    default List<ItemRequestDetailDto> findAllRequestsWithItems(Long userId) {
        List<ItemRequestDetailDto> requests = findAllRequestsWithoutItems(userId);
        requests.forEach(dto -> {
            List<ItemForRequestDto> items = findItemsForRequest(dto.getId());
            dto.setItems(items);
        });
        return requests;
    }
}
