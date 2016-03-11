package me.urbanowicz.samuel.stackoverflowcareers.service;

public class ServiceUtils {

    public static String getApiKey() {
        return "a13becac060949f08d6eae26467ca0e595afcb544a7497c0f0ba97de1987897cec8d360b8e52f0738063ad9c7c8e1c180b8b2746e8a44f3a61185e532c4401a2c6798d70b2e0a0cdb663bf748db2f12d";
    }

    public static String getUrlWithSearchQuery(String searchQuery) {
        return"webpage/url:http://careers.stackoverflow.com/jobs?searchTerm=" + searchQuery;
    }

    private ServiceUtils() {}

}
