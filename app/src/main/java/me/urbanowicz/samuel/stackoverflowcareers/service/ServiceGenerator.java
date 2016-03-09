package me.urbanowicz.samuel.stackoverflowcareers.service;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class ServiceGenerator {

    final static String BASE_URL = "https://api.import.io/store/connector/6986fa32-b64c-47d5-a1f4-3035affdf45b/";

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        return retrofit.create(serviceClass);
    }
}
