package com.example.appdominales.Controller;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.appdominales.Model.DBResultCallBack;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;

public class GestorDB {

    private static final String DB_NAME = "APPDominales";

    private static GestorDB mGestorDB;

    private GestorDB(){}

    public static GestorDB getGesorDB(){
        if(mGestorDB == null)
            mGestorDB = new GestorDB();
        return mGestorDB;
    }

    public String login(final Activity pActivity, String username, String password, final DBResultCallBack dbResultCallBack){

        HashMap<String,Object> mapData = new HashMap<>();
        mapData.put("jsonrpc", "2.0");

        HashMap<String,String> params = new HashMap<>();
        params.put("db", DB_NAME);
        params.put("login", username);
        params.put("password", password);

        JSONObject jsonParams = new JSONObject(params);

        mapData.put("params", jsonParams);

        JSONObject jsonData = new JSONObject(mapData);

        Data data = new Data.Builder()
                    .putString("method","POST")
                    .putString("url", "/web/session/authenticate")
                    .putString("data", jsonData.toString())
                    .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerAppdominales.class)
                .setConstraints(restricciones)
                .setInputData(data)
                .build();

        final String[] token = {""};
        WorkManager.getInstance(pActivity)
                .getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) pActivity, new Observer<WorkInfo>() {

                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String result = workInfo.getOutputData().getString("result");
                            if (result == null) return;

                            JSONParser parser = new JSONParser();
                            try {
                                JSONObject jsonResult = (JSONObject) parser.parse(result);

                                if(jsonResult.get("error") != null){
                                    Toast.makeText(pActivity, "Username or password wrong", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                token[0] = (String) ((JSONObject) jsonResult.get("result")).get("session_id");

                                HashMap<String,String> newResult = new HashMap<>();
                                newResult.put("token", token[0]);

                                dbResultCallBack.onGetResult(new JSONObject(newResult).toString());

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }
                    }
                });

        WorkManager.getInstance(pActivity).enqueue(otwr);

        Log.i("TOKEN","NADA");
        return username;
    }

}
