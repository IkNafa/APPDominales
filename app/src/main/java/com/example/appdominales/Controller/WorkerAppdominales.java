package com.example.appdominales.Controller;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkerAppdominales extends Worker {

    public static final String SERVER_URL = "http://138.68.176.157:8069";
    private Context context;

    public WorkerAppdominales(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            URL url = new URL(SERVER_URL + getInputData().getString("url"));


            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);



            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            String session_id = context.getSharedPreferences("appdominales",Context.MODE_PRIVATE).getString("session_id",null);

            if(session_id != null){
                Log.i("DB","Cookie:session_id=" + session_id);
                urlConnection.setRequestProperty("Cookie", "session_id=" + session_id);
            }

            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            urlConnection.setRequestMethod(getInputData().getString("method"));
            Log.i("DB","Method:" + getInputData().getString("method"));
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

                String setCookie = urlConnection.getHeaderField("Set-Cookie");
                Log.i("DB",setCookie);
                try {
                    String session_id_cookie = setCookie.split(";")[0].split("=")[1];
                    context.getSharedPreferences("appdominales",Context.MODE_PRIVATE).edit().putString("session_id",session_id_cookie).apply();
                }catch (NullPointerException ignored){}

                urlConnection.disconnect();
                return Result.success(new Data.Builder().putString("result", result.toString()).build());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
