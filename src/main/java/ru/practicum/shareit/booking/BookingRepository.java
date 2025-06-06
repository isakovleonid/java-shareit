package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long id, LocalDateTime currDate1, LocalDateTime currDate2);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime currDate);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime currDate);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus bookingStatus);

    List<Booking> findByItemIdAndBookerIdAndEndLessThanEqual(Long bookerId, Long authorId, LocalDateTime currDate);

     @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
             "and b.start =  (" +
             "            SELECT max(b2.start) FROM Booking b2 " +
             "            WHERE b2.item.id = b.item.id " +
             "                  and b2.end < :currDate" +
             "        ) " +
            "order by b.start desc " +
            "FETCH FIRST 1 ROW ONLY")
    Booking findLastUpToDateByItem(@Param("itemId") Long itemId, @Param("currDate") LocalDateTime date);

    @Query("select b " +
            "from Booking b " +
            "where b.item.id = :itemId " +
            "and b.start =  (" +
            "            SELECT min(b2.start) FROM Booking b2 " +
            "            WHERE b2.item.id = b.item.id " +
            "                  and b2.start > :currDate" +
            "        ) " +
            "order by b.start desc " +
            "FETCH FIRST 1 ROW ONLY")
    Booking findNextToDateByItem(@Param("itemId") Long itemId, @Param("currDate") LocalDateTime date);
}
