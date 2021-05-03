package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.Routine;

public class TrainingActivity extends AppCompatActivity {

    private RecyclerView routinesRecyclerView;
    private TextView trainingNameTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        routinesRecyclerView = findViewById(R.id.routinesRecyclerView);
        trainingNameTxtView = findViewById(R.id.trainingName);
        loadData(GestorUsuarios.getGestorUsuarios().getCurrentUser().getId(), getIntent().getExtras().getLong("training_id"));

    }

    public void loadData(long user_id, final long training_id){
        GestorDB.getGesorDB().getTrainingData(this, user_id, training_id, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                if(!result.equalsIgnoreCase("OK")){
                    return;
                }


                trainingNameTxtView.setText(GestorUsuarios.getGestorUsuarios().getCurrentUser().getTraining(training_id).getName());

                RoutineAdapterRecycler elAdaptadorRecycler = new RoutineAdapterRecycler(TrainingActivity.this, training_id);
                routinesRecyclerView.setAdapter(elAdaptadorRecycler);

                LinearLayoutManager elLayoutLineal = new LinearLayoutManager(TrainingActivity.this, LinearLayoutManager.VERTICAL, false);
                routinesRecyclerView.setLayoutManager(elLayoutLineal);
            }
        });
    }
}


class RoutineAdapterRecycler extends RecyclerView.Adapter<RoutineViewHolder>{

    private Routine[] routines;
    private Context context;
    private long training_id;

    public RoutineAdapterRecycler(Context context, long training_id) {
        this.context = context;
        this.training_id = training_id;
        routines = GestorUsuarios.getGestorUsuarios().getCurrentUser().getTraining(training_id).getRoutineList();
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.routine_card, parent, false);
        return new RoutineViewHolder(layoutFila);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        if(getItemCount() == 1){
            holder.topBar.setVisibility(View.INVISIBLE);
            holder.bottomBar.setVisibility(View.INVISIBLE);
        }else if(position == getItemCount()-1){
            holder.topBar.setVisibility(View.VISIBLE);
            holder.bottomBar.setVisibility(View.INVISIBLE);
        }else if(position == 0){
            holder.topBar.setVisibility(View.INVISIBLE);
            holder.bottomBar.setVisibility(View.VISIBLE);
        }else{
            holder.topBar.setVisibility(View.VISIBLE);
            holder.bottomBar.setVisibility(View.VISIBLE);
        }

        final Routine routine = routines[position];

        holder.dayTxtView.setText(routine.getDayString());
        holder.routineNameTxtView.setText(routine.getName());
        holder.numExercisesTxtView.setText(String.valueOf(routine.getExercise_count()));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(routine.getExercise_count() > 0) {
                    Intent i = new Intent(context, RoutineActivity.class);
                    i.putExtra("training_id", training_id);
                    i.putExtra("routine_day", routine.getDay());
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return routines.length;
    }
}

class RoutineViewHolder extends RecyclerView.ViewHolder{

    public View topBar,bottomBar, layout;
    public TextView dayTxtView, numExercisesTxtView, routineNameTxtView;

    public RoutineViewHolder(@NonNull View itemView) {
        super(itemView);
        layout = itemView;
        topBar = itemView.findViewById(R.id.topBar);
        bottomBar = itemView.findViewById(R.id.bottomBar);
        dayTxtView = itemView.findViewById(R.id.day);
        numExercisesTxtView = itemView.findViewById(R.id.numExercises);
        routineNameTxtView = itemView.findViewById(R.id.routineName);
    }
}