package com.maneth.zikhron.repo;

import com.maneth.zikhron.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<String>  findDistinctRoomTypes();

    List<Room> findAvailableRoomsByDatesAndTypes(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    List<Room> getAllAvailableRooms();
}
