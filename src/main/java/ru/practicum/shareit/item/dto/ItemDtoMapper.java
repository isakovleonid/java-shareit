package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.mapper.DTOMapper;

@Component
public class ItemDtoMapper implements DTOMapper<ItemDto, Item> {
    @Override
    public ItemDto toDTO(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setDescription(item.getDescription());

        return itemDto;
    }

    @Override
    public Item fromDTO(ItemDto itemDto) {
        Item item = Item.builder().build();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setDescription(itemDto.getDescription());

        return item;
    }
}
