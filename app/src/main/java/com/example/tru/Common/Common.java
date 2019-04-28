package com.example.tru.Common;

import com.example.tru.Remote.IGoogleAPI;
import com.example.tru.Remote.RetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleAPI() {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
