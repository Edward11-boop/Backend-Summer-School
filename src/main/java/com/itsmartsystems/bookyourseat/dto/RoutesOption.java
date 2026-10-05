package com.itsmartsystems.bookyourseat.dto;

public class RoutesOption {
    private String routeName ;
    private double distanceInMeters;
    private long durationInSeconds ;

    public RoutesOption(String routeName , double distanceInMeters , long durationInSeconds)
    {
        this.routeName = routeName;
        this.distanceInMeters = distanceInMeters;
        this.durationInSeconds = durationInSeconds;
    }
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public long getDurationInSeconds() {
        return durationInSeconds;
    }

    public void setDurationInSeconds(long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
    }

    @Override
    public String toString() {
        long hours = durationInSeconds / 3600;
        long minutes = (durationInSeconds % 3600) / 60;
        double km = distanceInMeters / 1000;
        return routeName + ": " + hours + "h " + minutes + "min, " + km + " km";
    }
}
