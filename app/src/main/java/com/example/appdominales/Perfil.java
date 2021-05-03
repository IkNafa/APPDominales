package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.Measure;
import com.example.appdominales.Model.UserGoal;
import com.example.appdominales.Model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Perfil extends AppCompatActivity {

    private long user_id;
    private ImageView profile_image;
    private TextView userNameTxt,
                     userEmailTxt,
                     userFollowersTxtView,
                     userFollowingTxtView,
                     userWeightTxtView,
                     userHeightTxtView,
                     userMeasurePhotoDateTxtView,
                     userLastMeasureDateTxtView,
                     userRolTxtView;
    private RecyclerView userGoalsRecycler, userTagsRecycler;
    private FloatingActionButton chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        user_id = getIntent().getExtras().getLong("user_id");

        profile_image = findViewById(R.id.profile_image);
        userNameTxt = findViewById(R.id.user_name);
        userEmailTxt = findViewById(R.id.user_email);
        userFollowersTxtView = findViewById(R.id.user_followers);
        userFollowingTxtView = findViewById(R.id.user_following);
        userWeightTxtView = findViewById(R.id.user_weight);
        userHeightTxtView = findViewById(R.id.user_height);
        userMeasurePhotoDateTxtView = findViewById(R.id.user_last_measure_photo);
        userLastMeasureDateTxtView = findViewById(R.id.user_last_measure);
        userRolTxtView = findViewById(R.id.user_rol);

        userGoalsRecycler = findViewById(R.id.user_goals_recyclerView);
        userTagsRecycler = findViewById(R.id.user_tags_recyclerView);

        chatButton = findViewById(R.id.chatActionButton);

        loadData();

    }

    public void logout(View v){
        GestorDB.getGesorDB().logOut(this, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                getSharedPreferences("appdominales", Context.MODE_PRIVATE).edit().remove("session_id").remove("user_id").apply();

                Intent intent = new Intent(Perfil.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadData(){
        final Usuario usuario = GestorUsuarios.getGestorUsuarios().getUsuario(user_id);
        if(!usuario.isLoaded()){
            GestorDB.getGesorDB().getUserData(this, user_id, new DBResultCallBack() {
                @Override
                public void onGetResult(String result) {
                    if(result.equalsIgnoreCase("OK")){
                        loadFields(usuario);
                    }
                }
            });
        }else{
            loadFields(usuario);
        }

    }

    private void loadFields(final Usuario usuario){
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

        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttp3Downloader(client)).build();
        picasso.load(usuario.getImage()).resize(84,84).centerCrop().into(profile_image);

        userNameTxt.setText(usuario.getName());
        userEmailTxt.setText(usuario.getEmail());
        userRolTxtView.setText(usuario.is_trainer()?"Entrenador":"Usuario");

        Measure measure = usuario.getCurrentMeasure();
        userWeightTxtView.setText(measure==null?"--":measure.getWeight() + " Kg");
        userHeightTxtView.setText(measure==null?"--":measure.getHeight() + " cm");
        userLastMeasureDateTxtView.setText(measure==null?"--":measure.getDate());
        userMeasurePhotoDateTxtView.setText(measure==null?"--":measure.getDate());

        GoalRecyclerAdapter elAdaptadorRecycler = new GoalRecyclerAdapter(usuario.getGoals());
        userGoalsRecycler.setAdapter(elAdaptadorRecycler);

        LinearLayoutManager elLayoutLineal = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        userGoalsRecycler.setLayoutManager(elLayoutLineal);

        TagRecyclerAdapter tagRecyclerAdapter = new TagRecyclerAdapter(usuario.getTags());
        userTagsRecycler.setAdapter(tagRecyclerAdapter);

        GridLayoutManager elLayoutRejillaIgual= new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);
        userTagsRecycler.setLayoutManager(elLayoutRejillaIgual);


        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Perfil.this, ChatActivity.class);
                i.putExtra("user_id",usuario.getId());
                startActivity(i);
            }
        });

        CardView logoutCard = findViewById(R.id.logoutCard);
        if(usuario.getId() == GestorUsuarios.getGestorUsuarios().getCurrentUser().getId()){
            logoutCard.setVisibility(View.VISIBLE);
            chatButton.setVisibility(View.GONE);
        }else{
            logoutCard.setVisibility(View.GONE);
            chatButton.setVisibility(View.VISIBLE);
        }

    }
}

class GoalRecyclerAdapter extends RecyclerView.Adapter<GoalViewHolder>{

    private UserGoal[] goals;

    public GoalRecyclerAdapter(UserGoal[] pGoals) {
        goals = pGoals;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_card_view, parent, false);
        GoalViewHolder evh = new GoalViewHolder(layoutFila);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        UserGoal goal = goals[position];
        holder.goalName.setText(goal.getName());
        holder.goalDescription.setText(goal.getDescription());
    }

    @Override
    public int getItemCount() {
        return goals.length;
    }
}

class GoalViewHolder extends RecyclerView.ViewHolder{

    TextView goalName;
    TextView goalDescription;

    public GoalViewHolder(@NonNull View itemView) {
        super(itemView);
        goalName = itemView.findViewById(R.id.user_goal_name);
        goalDescription = itemView.findViewById(R.id.user_goal_description);
    }
}

class TagRecyclerAdapter extends RecyclerView.Adapter<TagViewHolder>{

    private String[] tags;

    public TagRecyclerAdapter(String[] pTags) {
        tags = pTags;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutFila = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_card_view, parent, false);
        TagViewHolder evh = new TagViewHolder(layoutFila);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.tag_name.setText(tags[position]);
    }

    @Override
    public int getItemCount() {
        return tags.length;
    }
}

class TagViewHolder extends RecyclerView.ViewHolder{

    TextView tag_name;

    public TagViewHolder(@NonNull View itemView) {
        super(itemView);
        tag_name = itemView.findViewById(R.id.tag_name);
    }
}