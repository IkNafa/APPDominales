package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class Planificador extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String[] nombres = new String[]{"ENTRENAMIENTO 1", "ENTRENAMIENTO 2", "ENTRENAMIENTO 3"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planificador);

        recyclerView = findViewById(R.id.reciclerview);
        ElAdaptadorRecycler elAdaptadorRecycler = new ElAdaptadorRecycler(nombres);
        recyclerView.setAdapter(elAdaptadorRecycler);

        LinearLayoutManager elLayoutLineal = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(elLayoutLineal);


    }
}

class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder>{

    private String[] nombres;

    public ElAdaptadorRecycler(String[] pNombres){
        nombres = pNombres;
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_card, parent, false);
        ElViewHolder evh = new ElViewHolder(layoutFila);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        holder.trainingName.setText(nombres[position]);
    }

    @Override
    public int getItemCount() {
        return nombres.length;
    }
}

class ElViewHolder extends RecyclerView.ViewHolder{

    TextView trainingName;

    public ElViewHolder(View v){
        super(v);
        trainingName = v.findViewById(R.id.trainingName);
    }
}