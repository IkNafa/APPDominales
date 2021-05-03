package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.Exercise;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RoutineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private TextView trainingName;
    private long trainingId,routineDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        recyclerView = findViewById(R.id.exercisesRecyclerView);
        trainingName = findViewById(R.id.routineName);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            trainingId = bundle.getLong("training_id");
            routineDay = bundle.getLong("routine_day");
            loadData();
        }
    }

    public void loadData(){

        trainingName.setText(GestorUsuarios.getGestorUsuarios().getCurrentUser().getTraining(trainingId).getRoutine(routineDay).getName());

        ExerciseAdapterRecycler elAdaptadorRecycler = new ExerciseAdapterRecycler(this,trainingId, routineDay);
        recyclerView.setAdapter(elAdaptadorRecycler);

        LinearLayoutManager elLayoutLineal = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(elLayoutLineal);
    }
}

class ExerciseAdapterRecycler extends RecyclerView.Adapter<ExerciseViewHolder>{

    Exercise[] exercises;
    Context context;

    public ExerciseAdapterRecycler(Context context, long trainingId, long routineDay) {
        this.context = context;
        exercises = GestorUsuarios.getGestorUsuarios().getCurrentUser().getTraining(trainingId).getRoutine(routineDay).getExerciseList();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_card, parent, false);
        return new ExerciseViewHolder(layoutFila);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        final Exercise exercise = exercises[position];

        holder.exerciseName.setText(exercise.getName());

        final String session_id = context.getSharedPreferences("appdominales", Context.MODE_PRIVATE).getString("session_id", null);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Cookie", "session_id=" + session_id).build();
                return chain.proceed(request);
            }
        }).build();

        Picasso picasso = new Picasso.Builder(context).downloader(new OkHttp3Downloader(client)).build();
        picasso.load(exercise.getImage()).resize(84,84).centerCrop().into(holder.exerciseImage);

        holder.exerciseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DB", "URL->"+exercise.getExternal_video());
                if(exercise.getExternal_video()!=null && !exercise.getExternal_video().isEmpty()){
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(exercise.getExternal_video()));
                    context.startActivity(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return exercises.length;
    }
}


class ExerciseViewHolder extends RecyclerView.ViewHolder{

    public ImageView exerciseImage;
    public TextView exerciseName;

    public ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        exerciseImage = itemView.findViewById(R.id.exerciseImage);
        exerciseName = itemView.findViewById(R.id.exerciseName);
    }
}