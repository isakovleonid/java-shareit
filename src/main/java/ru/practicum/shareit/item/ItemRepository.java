package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Map;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User user);

    @Query("select i from Item i " +
            "where i.owner = :owner " +
            " and i.available = true " +
            " and (upper(i.name) like upper(concat('%', :text, '%')) " +
            "      or upper(i.description) like upper(concat('%', :text, '%')))")
    List<Item> search(@Param("owner") User user, @Param("text") String text);
}
