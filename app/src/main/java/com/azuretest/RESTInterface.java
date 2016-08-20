package com.azuretest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by elithe on 8/16/2016.
 */

public interface RESTInterface {

    @POST("photo")
    Call<String> postTest();
}

