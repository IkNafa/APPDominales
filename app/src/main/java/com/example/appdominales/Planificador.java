package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.Training;
import com.example.appdominales.Model.Usuario;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Planificador extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private TextView userNameTxtview,
                     userEmailTxtView;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        navigationView = findViewById(R.id.menuNavigationView);
        recyclerView = findViewById(R.id.reciclerview);
        drawerLayout = findViewById(R.id.menuDrawerLayout);
        userNameTxtview = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        userEmailTxtView = navigationView.getHeaderView(0).findViewById(R.id.user_email);
        userImage = navigationView.getHeaderView(0).findViewById(R.id.profile_image);

        ToolBar toolBar = (ToolBar) getSupportFragmentManager().findFragmentById(R.id.toolbar);
        toolBar.setMenuButtonOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Intent i = null;

                switch (item.getItemId()){
                    case R.id.perfil:
                        i = new Intent(Planificador.this, Perfil.class);
                        i.putExtra("user_id", GestorUsuarios.getGestorUsuarios().getCurrentUser().getId());
                        break;
                    case R.id.entrenador:
                        i = new Intent(Planificador.this, Perfil.class);
                        i.putExtra("user_id", GestorUsuarios.getGestorUsuarios().getCurrentUser().getTrainer().getId());
                        break;

                }

                if(i != null){
                    startActivity(i);
                }

                drawerLayout.close();
                return false;
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        loadUserTrainings();
    }

    private void loadUserTrainings(){
        GestorDB.getGesorDB().getUserTrainingList(this, GestorUsuarios.getGestorUsuarios().getCurrentUser().getId(), new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                ElAdaptadorRecycler elAdaptadorRecycler = new ElAdaptadorRecycler(Planificador.this);
                recyclerView.setAdapter(elAdaptadorRecycler);

                LinearLayoutManager elLayoutLineal = new LinearLayoutManager(Planificador.this, LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager(elLayoutLineal);

                userNameTxtview.setText(GestorUsuarios.getGestorUsuarios().getCurrentUser().getName());
                userEmailTxtView.setText(GestorUsuarios.getGestorUsuarios().getCurrentUser().getEmail());

                final String session_id = getSharedPreferences("appdominales", Context.MODE_PRIVATE).getString("session_id", null);
                if(session_id == null || session_id.isEmpty())
                    return;


                OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request().newBuilder().addHeader("Cookie", "session_id=" + session_id).build();
                        return chain.proceed(request);
                    }
                }).build();

                Picasso picasso = new Picasso.Builder(Planificador.this).downloader(new OkHttp3Downloader(client)).build();
                picasso.load(GestorUsuarios.getGestorUsuarios().getCurrentUser().getImage()).resize(84,84).centerCrop().into(userImage);

                Menu navMenu = navigationView.getMenu();
                MenuItem clientesMenu = navMenu.findItem(R.id.clientes);
                MenuItem entrenadorMenu = navMenu.findItem(R.id.entrenador);
                if(GestorUsuarios.getGestorUsuarios().getCurrentUser().is_trainer()){
                    clientesMenu.setVisible(GestorUsuarios.getGestorUsuarios().getCurrentUser().getClient_count()>0);
                    entrenadorMenu.setVisible(false);
                }else{
                    clientesMenu.setVisible(false);
                    entrenadorMenu.setVisible(GestorUsuarios.getGestorUsuarios().getCurrentUser().getTrainer()!=null);
                }

            }
        });
    }
}

class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder>{

    private Training[] trainings;
    private Context context;

    public ElAdaptadorRecycler(Context pContext){
        context = pContext;
        trainings = GestorUsuarios.getGestorUsuarios().getCurrentUser().getTrainingArray();
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.training_card, parent, false);
        return new ElViewHolder(layoutFila);
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        final Training training = trainings[position];
        holder.trainingName.setText(training.getName());
        holder.setTrainingClient(training.getClient());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TrainingActivity.class);
                i.putExtra("training_id", training.getId());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trainings.length;
    }
}

class ElViewHolder extends RecyclerView.ViewHolder{

    View layout;
    TextView trainingName;
    TextView clientName;
    TextView clientLabel;

    public ElViewHolder(View v){
        super(v);
        layout = v;
        clientName = v.findViewById(R.id.trainingClientName);
        trainingName = v.findViewById(R.id.trainingName);
        clientLabel = v.findViewById(R.id.trainingClientLabel);
    }

    public void setTrainingClient(Usuario pClient){
        if(pClient != null){
            clientLabel.setVisibility(View.VISIBLE);
            clientName.setVisibility(View.VISIBLE);
            clientName.setText(pClient.getName());
        }else{
            clientLabel.setVisibility(View.GONE);
            clientName.setVisibility(View.GONE);
        }
    }
}