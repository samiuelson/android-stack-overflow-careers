package me.urbanowicz.samuel.stackoverflowjobs.data;

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

    @Override
    public boolean equals(Object o) {
        if(Search.this == o) {
            return true;
        }
        if (!(o instanceof Search)) {
            return false;
        }
        Search another = (Search) o;
        if (!this.jobTitle.equalsIgnoreCase(another.getJobTitle())) {
            return false;
        }
        if (this.allowsRemote != another.allowsRemote) {
            return false;
        }
        if (!this.location.equalsIgnoreCase(another.getLocation())) {
            return false;
        }
        if (this.providesRelocation != another.providesRelocation) {
            return false;
        }
        if (this.providesVisaSponsorship != another.providesVisaSponsorship) {
            return false;
        }
        return true;
    }

    public final static Search EMPTY = new Search("", "", 100, "", false, false, false);
}
