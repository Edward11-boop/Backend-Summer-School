package com.itsmartsystems.bookyourseat.repository;

import com.itsmartsystems.bookyourseat.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByCode(String code);

    default Room findByCod(String code) {
        return findByCode(code).orElse(null);
    }

    List<Room> findByFloorId(Long floorId);

    List<Room> findByType(String type);

    List<Room> findByCapacityGreaterThanEqual(Long capacity);

    boolean existsByCode(String code);
}