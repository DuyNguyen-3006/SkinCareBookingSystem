package com.skincare_booking_system.repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skincare_booking_system.entities.Slot;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    Slot findSlotBySlotid(Long slotid);

    @Query(value = "select * from slot where slot.slottime >= ?1\n" + "order by slottime asc", nativeQuery = true)
    List<Slot> getAllSlotCanBooking(LocalTime time);

    @Query(value = "select * from slot", nativeQuery = true)
    Set<Slot> getAllSlot();

    //get slot avtice trong khung gio hoat dong cua spa
    @Query(value = "SELECT * FROM slot s WHERE s.deleted = false " +
            "AND s.slottime BETWEEN '09:00:00' AND '19:00:00' " +
            "ORDER BY s.slottime ASC", nativeQuery = true)
    List<Slot> getAllSlotActive();

    @Query(value = "select * from slot s where minute(s.slottime) = 0", nativeQuery = true)
    List<Slot> getSlotsWithoutMinute();

    @Query(value = "select * from slot s where s.deleted = false \n" +
            "order by s.slottime asc limit 1 ",nativeQuery = true)
    Slot slotBeginActive();

    @Query(value = "select * from slot s where s.deleted = false and s.slottime > ?1 \n" +
            "order by s.slottime asc limit 1 ",nativeQuery = true)
    Slot slotAfterBeginActive(LocalTime time);

    @Query(value = "select * from slot s where s.deleted = false \n" +
            "order by s.slottime desc limit 1 ",nativeQuery = true)
    Slot slotEndActive();

    Slot findBySlottime(LocalTime time);
}
