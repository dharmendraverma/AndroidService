package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    ServiceWorker serviceWorker1 = new ServiceWorker("service_worker_1");
    ServiceWorker serviceWorker2 = new ServiceWorker("service_worker_2");

    private static final String initialUrl = "https://placehold.it/120x120&text=image";

    private String IMAGE_1;
    private String IMAGE_2;

    private ImageView imageView1;
    private ImageView imageView2;

    private int Counter1 = 0;
    private int Counter2 = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fetchImage1AndSet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    fetchImage2AndSet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fetchImage1AndSet() throws InterruptedException {
        IMAGE_1 = initialUrl + (++Counter1);
        serviceWorker1.addTask(new Task<Bitmap>() {
            @Override
            public Bitmap onExecuteTask() {
                //Fetching image1 through okhttp
                Bitmap  bitmap = null;
                try {
                    Request request = new Request.Builder().url(IMAGE_1).build();
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Response response = okHttpClient.newCall(request).execute();
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {

                }
                return bitmap;
            }

            @Override
            public void onTaskComplete(Bitmap result) {
                //Set bitmap to imageview
                imageView1.setImageBitmap(result);
            }
        });
    }

    private void fetchImage2AndSet() throws InterruptedException {
        IMAGE_2 = initialUrl + (++Counter2);
        serviceWorker2.addTask(new Task<Bitmap>() {
            @Override
            public Bitmap onExecuteTask() {
                //Fetching image1 through okhttp
                Bitmap  bitmap = null;
                try {
                    Request request = new Request.Builder().url(IMAGE_2).build();
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Response response = okHttpClient.newCall(request).execute();
                    bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                } catch (Exception e) {

                }
                return bitmap;
            }

            @Override
            public void onTaskComplete(Bitmap result) {
                //Set bitmap to imageview
                imageView2.setImageBitmap(result);
            }
        });
    }
}
