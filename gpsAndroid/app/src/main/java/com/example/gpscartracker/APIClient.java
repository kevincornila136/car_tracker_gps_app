package com.example.gpscartracker;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIClient {


    private static final String SERVER_URL = "http://a.b.c.d:8080/api/location"; // IP SERVER TAILSCALE
    private final OkHttpClient client = new OkHttpClient();

    public void sendLocation(String json, Callback callback) {
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(SERVER_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}