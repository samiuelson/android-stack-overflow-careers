package me.urbanowicz.samuel.stackoverflowcareers.service;

import com.google.gson.JsonElement;

import me.urbanowicz.samuel.stackoverflowcareers.domain.JobPostsFeed;
import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface JobPostFeedClient {

    String FeedQueryPath = "_query?input=webpage/url=http://careers.stackoverflow.com/jobs?searchTerm=&&_apikey=a13becac060949f08d6eae26467ca0e595afcb544a7497c0f0ba97de1987897cec8d360b8e52f0738063ad9c7c8e1c180b8b2746e8a44f3a61185e532c4401a2c6798d70b2e0a0cdb663bf748db2f12d";
    String FeedForTermQueryPath = "_query?input=webpage/";
    @GET(FeedQueryPath)
    Call<JsonElement> getJobPostsJsonCall();

    @GET(FeedQueryPath)
    Call<JsonElement> getJobPostsJsonCall(@Query("url") String searchTerm, @Query("_apikey") String apiKey);

    @GET(FeedQueryPath)
    Call<JobPostsFeed> getJobPostFeedCall();

    @GET(FeedForTermQueryPath)
    Call<JobPostsFeed> getJobPostFeedCall(@Query("url") String searchTerm, @Query("_apikey") String apiKey);
}
