package com.itsmartsystems.bookyourseat.dto;

public class ColleaguePreferenceDto {
    private String id;
    private String name;
    private int presencePercent;
    private int daysPresent;
    private String favoriteRoom;
    private String favoriteSeat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPresencePercent() {
        return presencePercent;
    }

    public void setPresencePercent(int presencePercent) {
        this.presencePercent = presencePercent;
    }

    public int getDaysPresent() {
        return daysPresent;
    }

    public void setDaysPresent(int daysPresent) {
        this.daysPresent = daysPresent;
    }

    public String getFavoriteRoom() {
        return favoriteRoom;
    }

    public void setFavoriteRoom(String favoriteRoom) {
        this.favoriteRoom = favoriteRoom;
    }

    public String getFavoriteSeat() {
        return favoriteSeat;
    }

    public void setFavoriteSeat(String favoriteSeat) {
        this.favoriteSeat = favoriteSeat;
    }
}