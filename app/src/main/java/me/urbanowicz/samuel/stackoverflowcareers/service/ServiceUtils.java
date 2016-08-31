package me.urbanowicz.samuel.stackoverflowcareers.service;

import me.urbanowicz.samuel.stackoverflowcareers.data.Search;

public class ServiceUtils {

    public static String getApiKey() {
        return "a13becac060949f08d6eae26467ca0e595afcb544a7497c0f0ba97de1987897cec8d360b8e52f0738063ad9c7c8e1c180b8b2746e8a44f3a61185e532c4401a2c6798d70b2e0a0cdb663bf748db2f12d";
    }

    public static String getUrlWithSearchQuery(
            String jobTitle,
            String location,
            int distance,
            String distanceUnits,
            String /*boolean*/ allowsRemote,
            String /*boolean*/ providesRelocation,
            String /*boolean*/ providesVisaSponsorship,
            int page
    ) {
        return new StringBuilder("webpage/url:http://careers.stackoverflow.com/jobs?")
                .append("searchTerm=").append(jobTitle)
                .append("&location=").append(location)
                .append("&range=").append(distance)
                .append("&distanceUnits=").append(distanceUnits)
                .append("&allowsremote=").append(allowsRemote)
                .append("&offersrelocation=").append(providesRelocation)
                .append("&offersvisasponsorship=").append(providesVisaSponsorship)
                .append("&pg=").append(String.valueOf(page))
                .toString();
    }

    public static String getUrlSearchQuery(Search search, int page) {
        return getUrlWithSearchQuery(
                search.getJobTitle(),
                search.getLocation(),
                search.getDistance(),
                search.getDistanceUnits(),
                search.isAllowsRemote()? "true" : "",
                search.isProvidesRelocation()? "true" : "",
                search.isProvidesVisaSponsorship()? "true" : "",
                page
        );
    }

    public static class SearchQueryUrlBuilder {
        private String jobTitle = "";
        private String location = "";
        private int distance = 0;
        private String distanceUnits = "";
        private boolean allowsRemote = false;
        private boolean providesRelocation = false;
        private boolean providesVisaSponsorship = false;
        private int page = 0;

        public SearchQueryUrlBuilder addJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public SearchQueryUrlBuilder addLocation(String location) {
            this.location = location;
            return this;
        }

        public SearchQueryUrlBuilder addDistance(int distance) {
            this.distance = distance;
            return this;
        }

        public SearchQueryUrlBuilder addDistanceUnits(String distanceUnits) {
            this.distanceUnits = distanceUnits;
            return this;
        }

        public SearchQueryUrlBuilder addAllowsRemote(boolean allowsRemote) {
            this.allowsRemote = allowsRemote;
            return this;
        }

        public SearchQueryUrlBuilder addProvidesRelocation(boolean providesRelocation) {
            this.providesRelocation = providesRelocation;
            return this;
        }

        public SearchQueryUrlBuilder addProvidesVisaSponsorship(boolean providesVisaSponsorship) {
            this.providesVisaSponsorship = providesVisaSponsorship;
            return this;
        }

        public SearchQueryUrlBuilder addPage(int page) {
            this.page = page;
            return this;
        }

        @Override
        public String toString() {
            return getUrlWithSearchQuery(
                    jobTitle,
                    location,
                    distance,
                    distanceUnits,
                    allowsRemote? "true" : "",
                    providesRelocation? "true" : "",
                    providesVisaSponsorship? "true" : "",
                    page
            );
        }
    }

    private ServiceUtils() {
    }

}
