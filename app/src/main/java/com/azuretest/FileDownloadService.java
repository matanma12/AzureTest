package com.azuretest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by elithe on 8/21/2016.
 */
public interface FileDownloadService {
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
