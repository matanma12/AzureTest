package com.azuretest;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private RESTInterface mRESTInterface;
    final String TAG = "MainActivity";
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("http://mongod.eastus.cloudapp.azure.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mRESTInterface = restAdapter.create(RESTInterface.class);

        verifyStoragePermissions(this);

    }

    public void upload(View view) {

        Uri uri = Uri.parse("file:/storage/emulated/0/DCIM/Camera/IMG_20151127_185848.JPG");
        uploadFile(uri);

        /*
        working code for regular rest interface POSTd
        Call<String> call = mRESTInterface.postTest();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i(TAG,"SUCCESS!!! with resp: "  + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.i(TAG,"onFailure!!!"  );
            }
        });*/

    }
    private void uploadFile(Uri fileUri) {
        // create upload service client
        FileUploadService service =
                ServiceGenerator.createService(FileUploadService.class);

        File file = new File(fileUri.getPath());
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
        RequestBody userName =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), "elithe1");

        String fullPathString = "media/f8e11adf-32ac-4e86-a854-67930a675b1f.JPG";
        RequestBody fullPath =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), fullPathString);

        String  storyID= "173372397498439024";
        RequestBody storyid =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), storyID);

        String  type= "photo";
        RequestBody storytype =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), type);

        // finally, execute the request
        Log.i(TAG, "Starting uploading mediaFile........");
        Call<ResponseBody> call = service.upload(userName, fullPath, storyid, storytype, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                Log.i(TAG, "Upload: success with resp code: " + response.code() + " resp body: " + response.raw());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Upload error:" + t.getMessage());
            }
        });
    }

    public void downloadFile(View view) {
        String fileUrl = "http://mongod.eastus.cloudapp.azure.com/download";

        FileDownloadService downloadService = ServiceGenerator.createService(FileDownloadService.class);

        Log.d(TAG, "Started file download");

        Call<ResponseBody> call = downloadService.downloadFile(fileUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body());

                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "FutureStudioIcon.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                Picasso.with(this).load(futureStudioIconFile).into(imageView);

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }












    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }





}
