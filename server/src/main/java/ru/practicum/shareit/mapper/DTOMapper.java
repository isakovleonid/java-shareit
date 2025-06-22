package ru.practicum.shareit.mapper;

public interface DTOMapper<T, V> {
    public T toDTO(V v);

    public V fromDTO(T t);
}