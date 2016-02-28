package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;

import retrofit.Call;
import retrofit.http.GET;

public interface JobPostFeedApi {
    @GET("_query?input=webpage/url:http://careers.stackoverflow.com/jobs?searchTerm=&&_apikey=a13becac060949f08d6eae26467ca0e595afcb544a7497c0f0ba97de1987897cec8d360b8e52f0738063ad9c7c8e1c180b8b2746e8a44f3a61185e532c4401a2c6798d70b2e0a0cdb663bf748db2f12d")
    Call<JsonElement> getJobPosts();
}
