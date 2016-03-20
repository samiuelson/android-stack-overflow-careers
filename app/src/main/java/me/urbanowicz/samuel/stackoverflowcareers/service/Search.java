package me.urbanowicz.samuel.stackoverflowcareers.service;

import java.io.Serializable;

public class Search implements Serializable {
    private final String jobTitle;
    private final String location;
    private final int distance;
    private final String distanceUnits;
    private final boolean allowsRemote;
    private final boolean providesRelocation;
    private final boolean providesVisaSponsorship;

    public Search(String jobTitle,
                  String location,
                  int distance,
                  String distanceUnits,
                  boolean allowsRemote,
                  boolean providesRelocation,
                  boolean providesVisaSponsorship) {

        this.jobTitle = jobTitle;
        this.location = location;
        this.distance = distance;
        this.distanceUnits = distanceUnits;
        this.allowsRemote = allowsRemote;
        this.providesRelocation = providesRelocation;
        this.providesVisaSponsorship = providesVisaSponsorship;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public int getDistance() {
        return distance;
    }

    public String getDistanceUnits() {
        return distanceUnits;
    }

    public boolean isAllowsRemote() {
        return allowsRemote;
    }

    public boolean isProvidesRelocation() {
        return providesRelocation;
    }

    public boolean isProvidesVisaSponsorship() {
        return providesVisaSponsorship;
    }

    public final static Search EMPTY = new Search("", "", 100, "", false, false, false);
}
