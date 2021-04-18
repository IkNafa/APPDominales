package com.example.appdominales.Controller;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WorkerAppdominales extends Worker {

    public static final String SERVER_URL = "http://138.68.176.157:8069";

    public WorkerAppdominales(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            URL url = new URL(SERVER_URL + getInputData().getString("url"));
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            urlConnection.setRequestMethod(getInputData().getString("method"));
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(getInputData().getString("data"));
            out.close();

            int statusCode = urlConnection.getResponseCode();

            if(statusCode == 200){
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                StringBuilder result = new StringBuilder();
                while((line = bufferedReader.readLine()) != null){
                    result.append(line);
                }
                inputStream.close();

                return Result.success(new Data.Builder().putString("result", result.toString()).build());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
