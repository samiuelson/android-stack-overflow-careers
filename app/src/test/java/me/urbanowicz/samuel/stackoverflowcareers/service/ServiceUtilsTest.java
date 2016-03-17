package me.urbanowicz.samuel.stackoverflowcareers.service;

import static junit.framework.Assert.*;

import org.junit.Test;

public class ServiceUtilsTest {

    @Test
    public void
    shouldBuildProperUrl() {
        ServiceUtils.SearchQueryUrlBuilder urlBuilder = new ServiceUtils.SearchQueryUrlBuilder();
        final String url = urlBuilder
                .addAllowsRemote(true)
                .addDistance(100)
                .addLocation("London")
                .addDistanceUnits("km")
                .addJobTitle("android")
                .addProvidesRelocation(true)
                .addProvidesVisaSponsorship(false)
                .toString();

        assertNotNull(url);
        assertEquals(url, "webpage/url:http://careers.stackoverflow.com/jobs?searchTerm=android&location=London&range=100&distanceUnits=km&allowsremote=true&offersrelocation=true&offersvisasponsorship=false");
    }
}
