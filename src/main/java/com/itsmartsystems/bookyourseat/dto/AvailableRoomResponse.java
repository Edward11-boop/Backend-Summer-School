package com.itsmartsystems.bookyourseat.dto;

import com.itsmartsystems.bookyourseat.model.Room;

public record AvailableRoomResponse(Long id, String code, String name, String type, FloorResponse floor) {
    public AvailableRoomResponse(Room room) {
        this(room.getId(), room.getCode(), room.getName(), room.getType(),
                room.getFloor_id() == null ? null : new FloorResponse(room.getFloor_id()));
    }

    public record FloorResponse(Long id, String name) {
        public FloorResponse(com.itsmartsystems.bookyourseat.model.Floor floor) {
            this(floor.getId(), floor.getName());
        }
    }
}