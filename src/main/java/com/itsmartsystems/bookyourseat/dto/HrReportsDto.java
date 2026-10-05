package com.itsmartsystems.bookyourseat.dto;

import java.util.List;

public class HrReportsDto {
    private List<MonthlyTrafficDto> monthlyTraffic;
    private List<ZonePopularityDto> zonePopularity;
    private List<RoomUtilizationDto> roomsUtilization;

    public List<MonthlyTrafficDto> getMonthlyTraffic() {
        return monthlyTraffic;
    }

    public void setMonthlyTraffic(List<MonthlyTrafficDto> monthlyTraffic) {
        this.monthlyTraffic = monthlyTraffic;
    }

    public List<ZonePopularityDto> getZonePopularity() {
        return zonePopularity;
    }

    public void setZonePopularity(List<ZonePopularityDto> zonePopularity) {
        this.zonePopularity = zonePopularity;
    }

    public List<RoomUtilizationDto> getRoomsUtilization() {
        return roomsUtilization;
    }

    public void setRoomsUtilization(List<RoomUtilizationDto> roomsUtilization) {
        this.roomsUtilization = roomsUtilization;
    }
}