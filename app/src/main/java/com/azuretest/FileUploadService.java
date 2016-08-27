package com.azuretest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**dfd
 * Created by elithe on 8/18/2016.
 */
public interface FileUploadService {
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("userName") RequestBody userName,
                              @Part("fullPath") RequestBody fullPath,
                              @Part("storyID") RequestBody storyID,
                              @Part("type") RequestBody type,
                              @Part MultipartBody.Part file);
}