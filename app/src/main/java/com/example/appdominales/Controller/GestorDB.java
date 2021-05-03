package com.example.appdominales.Controller;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.appdominales.Model.ChatMessage;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.Exercise;
import com.example.appdominales.Model.Measure;
import com.example.appdominales.Model.OdooResult;
import com.example.appdominales.Model.Routine;
import com.example.appdominales.Model.Training;
import com.example.appdominales.Model.UserGoal;
import com.example.appdominales.Model.Usuario;
import com.facebook.HttpMethod;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Objects;

public class GestorDB {

    private static final String DB_NAME = "appdominales";

    private static GestorDB mGestorDB;

    private GestorDB(){}

    public static GestorDB getGesorDB(){
        if(mGestorDB == null)
            mGestorDB = new GestorDB();
        return mGestorDB;
    }

    public void loginWithEmail(final Activity pActivity, final String username, String password, final DBResultCallBack dbResultCallBack){
        login(pActivity, username, password, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result == null) {
                    dbResultCallBack.onGetResult(OdooResult.SYSTEM_ERROR.toString());
                    return;
                }

                JSONParser parser = new JSONParser();
                try {
                    JSONObject jsonResult = (JSONObject) parser.parse(result);
                    if(jsonResult.containsKey("error")){
                        JSONObject error = (JSONObject) jsonResult.get("error");
                        JSONObject error_data = (JSONObject) error.get("data");
                        String exeption_type = (String) error_data.get("exception_type");
                        assert exeption_type != null;
                        if(exeption_type.equalsIgnoreCase("access_denied")){
                            dbResultCallBack.onGetResult(OdooResult.ACCESS_DENIED.toString());
                        }
                        return;
                    }

                    if(jsonResult.containsKey("result")){
                        JSONObject userData = (JSONObject) jsonResult.get("result");

                        loadUserData(pActivity, userData, true);

                        dbResultCallBack.onGetResult("OK");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    dbResultCallBack.onGetResult(OdooResult.SYSTEM_ERROR.toString());
                }

            }
        });
    }

    public void loginWithProvider(final Activity pActivity, String username, String pass, final DBResultCallBack dbResultCallBack){
        loginWithEmail(pActivity, username, pass, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result == null)
                    return;
                if(result.equalsIgnoreCase("OK")){
                    dbResultCallBack.onGetResult(result);
                    return;
                }
                if(result.equals(OdooResult.ACCESS_DENIED.toString())){
                    //REGISTRAR
                    throw new UnsupportedOperationException();
                }
            }
        });
    }

    public void login(final Activity pActivity, String username, String password, DBResultCallBack dbResultCallBack){

        String token = FirebaseMessaging.getInstance().getToken().getResult();

        HashMap<String,String> params = new HashMap<>();
        params.put("db", DB_NAME);
        params.put("login", username);
        params.put("password", password);
        params.put("token", token);

        Log.i("DB",token);

        JSONObject jsonParams = new JSONObject(params);

        String url = "/api/session/authenticate";
        String method = "POST";

        odooRequest(pActivity, method, url, jsonParams, dbResultCallBack);
    }

    public void getCurrentUserData(final Activity pActivity, final DBResultCallBack dbResultCallBack){
        String url = "/api/users/me";
        String method = "POST";
        JSONObject params = new JSONObject();

        odooRequest(pActivity, method, url, params, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result == null){
                    dbResultCallBack.onGetResult(OdooResult.SYSTEM_ERROR.toString());
                    return;
                }

                Log.i("DB", result);

                JSONParser parser = new JSONParser();
                JSONObject resultJson;
                try {
                    resultJson = (JSONObject) parser.parse(result);
                    if(resultJson.containsKey("error")){
                        return;
                    }

                    JSONObject userData = (JSONObject) resultJson.get("result");

                    assert userData != null;
                    loadUserData(pActivity, userData, true);

                    dbResultCallBack.onGetResult("OK");

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void getUserData(final Activity pActivity, long user_id, final DBResultCallBack dbResultCallBack){
        String url = "/api/users/" + String.valueOf(user_id);
        String method = "POST";
        JSONObject params = new JSONObject();

        odooRequest(pActivity, method, url, params, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result == null){
                    dbResultCallBack.onGetResult(OdooResult.SYSTEM_ERROR.toString());
                    return;
                }

                Log.i("DB", result);

                JSONParser parser = new JSONParser();
                JSONObject resultJson = null;
                try {
                    resultJson = (JSONObject) parser.parse(result);
                    if(resultJson.containsKey("error")){
                        return;
                    }

                    JSONObject userData = (JSONObject) resultJson.get("result");

                    assert userData != null;
                    loadUserData(pActivity, userData, false);

                    dbResultCallBack.onGetResult("OK");

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

    }



    public void getUserTrainingList(final Activity pActivity, final long user_id, final DBResultCallBack dbResultCallBack){
        String url = "/api/users/" + String.valueOf(user_id) + "/trainings";
        String method = HttpMethod.POST.name();
        JSONObject params = new JSONObject();

        odooRequest(pActivity, method, url, params, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(result == null) {
                    dbResultCallBack.onGetResult(OdooResult.SYSTEM_ERROR.toString());
                    return;
                }

                JSONParser parser = new JSONParser();
                try {
                    JSONObject resultJson = (JSONObject) parser.parse(result);

                    if(resultJson.containsKey("error")){
                        return;
                    }

                    JSONArray training_ids = (JSONArray) resultJson.get("result");

                    for(int i = 0; i< (training_ids != null ? training_ids.size() : 0); i++){
                        JSONObject training_id = (JSONObject) training_ids.get(i);

                        String name = (String) training_id.get("name");
                        long id = (long) training_id.get("id");

                        JSONObject trainer = (JSONObject) training_id.get("trainer");
                        long trainer_id = (long)  trainer.get("id");
                        String trainer_name = (String) trainer.get("name");
                        String trainer_email = (String) trainer.get("email");

                        Training training;
                        if(training_id.containsKey("client")){
                            JSONObject client = (JSONObject) training_id.get("client");
                            long client_id = (long) client.get("id");
                            String client_name = (String) client.get("name");
                            String client_email = (String) client.get("email");

                            Usuario trainerUser = GestorUsuarios.getGestorUsuarios().getUsuario(trainer_id) != null? GestorUsuarios.getGestorUsuarios().getUsuario(trainer_id):new Usuario(trainer_id,trainer_name, trainer_email);
                            Usuario clientUser = GestorUsuarios.getGestorUsuarios().getUsuario(trainer_id) != null? GestorUsuarios.getGestorUsuarios().getUsuario(client_id):new Usuario(client_id,client_name, client_email);

                            GestorUsuarios.getGestorUsuarios().addUser(trainerUser);
                            GestorUsuarios.getGestorUsuarios().addUser(clientUser);

                            training = new Training(id, name,trainerUser, clientUser);
                        }else{
                            training = new Training(id, name, GestorUsuarios.getGestorUsuarios().getUsuario(id));
                        }

                        GestorUsuarios.getGestorUsuarios().addTrainingToUser(user_id, training);

                    }

                    dbResultCallBack.onGetResult("OK");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void getTrainingData(Activity pActivity, final long user_id, final long training_id, final DBResultCallBack dbResultCallBack){
        String url = "/api/trainings/" + String.valueOf(training_id);
        String method = String.valueOf(HttpMethod.POST);
        JSONObject params = new JSONObject();

        odooRequest(pActivity, method, url, params, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                JSONParser parser = new JSONParser();
                try {
                    JSONObject resultJson = (JSONObject) parser.parse(result);

                    if (resultJson.containsKey("error")) {
                        return;
                    }

                    JSONArray routineListJson = (JSONArray) resultJson.get("result");
                    loadTrainingData(user_id, training_id, routineListJson);
                    dbResultCallBack.onGetResult("OK");
                }catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    public void logOut(Activity pActivity, DBResultCallBack dbResultCallBack){
        String url = "/web/session/destroy";
        String method = HttpMethod.POST.name();
        JSONObject params = new JSONObject();

        odooRequest(pActivity, method, url, params, dbResultCallBack);
    }

    private void odooRequest(final Activity pActivity, String method, String url, JSONObject params, final DBResultCallBack dbResultCallBack){

        HashMap<String,Object> mapData = new HashMap<>();
        mapData.put("jsonrpc", "2.0");

        mapData.put("params", params);

        JSONObject jsonData = new JSONObject(mapData);

        Data data = new Data.Builder()
                .putString("method",method)
                .putString("url", url)
                .putString("data", jsonData.toString())
                .build();

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(WorkerAppdominales.class)
                .setConstraints(restricciones)
                .setInputData(data)
                .build();

        WorkManager.getInstance(pActivity)
                .getWorkInfoByIdLiveData(otwr.getId())
                .observe((LifecycleOwner) pActivity, new Observer<WorkInfo>() {

                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String result = workInfo.getOutputData().getString("result");
                            if (result == null) return;
                            dbResultCallBack.onGetResult(result);
                        }
                    }
                });

        WorkManager.getInstance(pActivity).enqueue(otwr);
    }

    private void loadUserData(Activity pActivity, JSONObject result, boolean current_user){
        String name = (String) result.get("name");
        long id = (long) result.get("id");
        String birthday = (String) result.get("birthday");
        String email = (String) result.get("email");
        String provider = (String) result.get("provider");
        boolean is_trainer = (boolean) result.get("is_trainer");
        String image = (String) result.get("image");
        String function = (String) result.get("function");
        String gender = (String) result.get("gender");
        String phone = (String) result.get("phone");

        Usuario user = GestorUsuarios.getGestorUsuarios().getUsuario(id) != null? GestorUsuarios.getGestorUsuarios().getUsuario(id):new Usuario(id, name,email);
        user.setBirthday(birthday);
        user.setFunction(function);
        user.setProvider(provider);
        user.setImage(image);
        user.setGenero(gender);
        user.setPhone(phone);
        user.setIs_trainer(is_trainer);
        user.setLoaded(true);

        if(result.containsKey("trainer")){
            JSONObject trainerJson = (JSONObject) result.get("trainer");
            long trainer_id = (long) trainerJson.get("id");
            String trainer_name = (String) trainerJson.get("name");
            String trainer_email = (String) trainerJson.get("email");
            long trainer_client_count = (long) trainerJson.get("client_count");
            double trainer_rating_mean = (double) trainerJson.get("rating_mean");

            Usuario trainer = GestorUsuarios.getGestorUsuarios().getUsuario(trainer_id);
            trainer = trainer!=null?trainer:new Usuario(trainer_id,trainer_name,trainer_email);
            trainer.setClient_count(trainer_client_count);
            trainer.setRating_mean(trainer_rating_mean);

            GestorUsuarios.getGestorUsuarios().addUser(trainer);

            user.setTrainer(trainer);

        }

        user.clearGoals();
        if(result.containsKey("goals")){
            JSONArray goalsArray = (JSONArray) result.get("goals");
            for(int i = 0; i< Objects.requireNonNull(goalsArray).size(); i++){
                JSONObject goalJson = (JSONObject) goalsArray.get(i);
                String goal_name = (String) goalJson.get("name");
                String goal_description = (String) goalJson.get("description");

                UserGoal goal = new UserGoal(goal_name, goal_description);

                user.addGoal(goal);
            }
        }

        user.clearMeasures();
        if(result.containsKey("current_measure")){
            JSONObject measureJson = (JSONObject) result.get("current_measure");
            long measure_id = (long) measureJson.get("id");
            long height = (long) measureJson.get("height");
            double weight = (double) measureJson.get("weight");
            String measure_date = (String) measureJson.get("date");

            Measure measure = user.getMeasure(measure_id)!=null?user.getMeasure(measure_id):new Measure(measure_id, measure_date, weight, height);
            user.setCurrentMeasure(measure);

        }

        user.clearTags();
        if(result.containsKey("tags")){
            JSONArray tagsJson = (JSONArray) result.get("tags");
            for(int i = 0; i< Objects.requireNonNull(tagsJson).size(); i++){
                JSONObject tagJson = (JSONObject) tagsJson.get(i);
                String tag_name = (String) tagJson.get("name");
                user.addTag(tag_name);
            }
        }

        if(result.containsKey("clients")){
            JSONArray clientsJson = (JSONArray) result.get("clients");
            for(int i=0;i<clientsJson.size();i++){
                JSONObject clientJson = (JSONObject) clientsJson.get(i);
                long client_id = (long) clientJson.get("id");
                String client_name = (String) clientJson.get("name");
                String client_image = (String) clientJson.get("image");
                String client_email = (String) clientJson.get("email");

                Usuario client = GestorUsuarios.getGestorUsuarios().getUsuario(client_id);
                client = client!=null?client:new Usuario(client_id,client_name, client_email);
                client.setImage(client_image);

                user.addClient(client);
            }
        }

        GestorUsuarios.getGestorUsuarios().addUser(user);

        if(current_user){
            GestorUsuarios.getGestorUsuarios().setCurrentUser(user.getId());
            pActivity.getSharedPreferences("appdominales", Context.MODE_PRIVATE).edit().putLong("user_id",user.getId()).apply();
        }

    }

    public void loadTrainingData(long user_id, long training_id, JSONArray result){
        Training training = GestorUsuarios.getGestorUsuarios().getUsuario(user_id).getTraining(training_id);
        for(int i = 0; i<result.size();i++){
            JSONObject routineJson = (JSONObject) result.get(i);
            long id = (long) routineJson.get("id");
            String name = (String) routineJson.get("name");
            long dayIndex = (long) routineJson.get("day");
            long exercise_count = (long) routineJson.get("exercise_count");


            Routine routine = training.getRoutine(dayIndex)!=null? training.getRoutine(dayIndex):new Routine(id,name,dayIndex,exercise_count);

            JSONArray exercisesJSON = (JSONArray) routineJson.get("exercises");
            for(int j=0; j<exercisesJSON.size();j++){
                JSONObject exerciseJson = (JSONObject) exercisesJSON.get(j);
                String tempo = (String) exerciseJson.get("tempo");
                String image = (String) exerciseJson.get("image");
                if(image.isEmpty())
                    image = "https://png.pngtree.com/png-vector/20190115/ourlarge/pngtree-vector-exercise-icon-png-image_319652.jpg";
                String group = (String) exerciseJson.get("group");
                String description = (String) exerciseJson.get("description");
                String range = (String) exerciseJson.get("range");
                String bartype = (String) exerciseJson.get("bartype");
                String exercise_name = (String) exerciseJson.get("name");
                String grip = (String) exerciseJson.get("grip");
                String stance = (String) exerciseJson.get("stance");
                String external_video = (String) exerciseJson.get("external_video");

                Exercise exercise = new Exercise(exercise_name, description, external_video, image, tempo, group, range, bartype, grip, stance);

                JSONArray setsJson = (JSONArray) exerciseJson.get("sets");
                for(int k=0; k<setsJson.size();k++){
                    JSONObject setJson = (JSONObject) setsJson.get(k);
                    long reps = (long) setJson.get("reps");
                    double weight = (double) setJson.get("weight");
                    long rpe = (long) setJson.get("rpe");

                    ExerciseSet set = new ExerciseSet(reps, weight, rpe);

                    exercise.addSet(set);
                }


                routine.addExercise(exercise);
            }

            training.addRoutine(routine);
        }
    }

    public void loadChatMessages(Activity pActivity, final long user_id, final DBResultCallBack dbResultCallBack){
        HashMap<String, Long> params = new HashMap<>();
        params.put("user_id", user_id);

        JSONObject jsonParams = new JSONObject(params);

        odooRequest(pActivity, "POST", "/api/chat/get", jsonParams, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                JSONParser parser = new JSONParser();
                try {

                    JSONObject resultJson = (JSONObject) parser.parse(result);

                    if (resultJson.containsKey("error")) {
                        return;
                    }

                    JSONArray messagesArrayJson = (JSONArray) resultJson.get("result");

                    GestorUsuarios.getGestorUsuarios().getCurrentUser().clearMessages(user_id);
                    for(int i=0;i<messagesArrayJson.size();i++){
                        JSONObject messageJson = (JSONObject) messagesArrayJson.get(i);
                        String text = (String) messageJson.get("text");
                        String datetime = (String) messageJson.get("datetime");
                        JSONObject user = (JSONObject) messageJson.get("user");
                        long message_user_id = (long) user.get("id");
                        String user_name = (String) user.get("name");
                        String user_email = (String) user.get("email");

                        Usuario usuario = GestorUsuarios.getGestorUsuarios().getUsuario(message_user_id);
                        usuario = usuario!=null?usuario:new Usuario(message_user_id, user_name, user_email);

                        ChatMessage chatMessage = new ChatMessage(usuario, text, datetime);

                        GestorUsuarios.getGestorUsuarios().getCurrentUser().addChatMessage(user_id, chatMessage);

                    }

                    dbResultCallBack.onGetResult("OK");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendChatMessage(Activity pActivity, long user_id, String text, final DBResultCallBack dbResultCallBack){
        final HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("text", text);
        JSONObject jsonParams = new JSONObject(params);

        odooRequest(pActivity, "POST", "/api/chat/send", jsonParams, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                JSONParser parser = new JSONParser();
                JSONObject resultJson = null;
                try {
                    resultJson = (JSONObject) parser.parse(result);
                    if(resultJson.containsKey("error")){
                        return;
                    }

                    dbResultCallBack.onGetResult("OK");
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });
    }

}
