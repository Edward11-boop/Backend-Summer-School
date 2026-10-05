package com.itsmartsystems.bookyourseat.dto;

public class ZonePopularityDto {
    private String zone;
    private String occupancyRate;
    private String level;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(String occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}