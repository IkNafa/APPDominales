package com.example.appdominales;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appdominales.Controller.GestorDB;
import com.example.appdominales.Controller.GestorUsuarios;
import com.example.appdominales.Model.ChatMessage;
import com.example.appdominales.Model.DBResultCallBack;
import com.example.appdominales.Model.Usuario;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensajesRecyclerView;
    private ChatRecyclerAdapter adapter;
    private ImageView chatImage;
    private TextView chatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        long user_id = getIntent().getExtras().getLong("user_id");

        chatImage = findViewById(R.id.user_image);
        chatName = findViewById(R.id.user_name);
        mensajesRecyclerView = findViewById(R.id.listaMensajesForo);

        Usuario u = GestorUsuarios.getGestorUsuarios().getUsuario(user_id);

        chatName.setText(u.getName());

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
        picasso.load(u.getImage()).resize(84,84).centerCrop().into(chatImage);

        loadData(user_id);
    }

    public void loadData(final long user_id){
        GestorDB.getGesorDB().loadChatMessages(this, user_id, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {

                adapter = new ChatRecyclerAdapter(user_id);
                mensajesRecyclerView.setAdapter(adapter);

                LinearLayoutManager elLayoutLineal = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, true);
                mensajesRecyclerView.setLayoutManager(elLayoutLineal);

                mensajesRecyclerView.addItemDecoration(new GridSpacingItemDecoration(1, 20, true));

                final EditText messageEditText = findViewById(R.id.mensajeAMandar);
                ImageView sendMessagebutton = findViewById(R.id.botonMandarMensajeForo);
                sendMessagebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(messageEditText.getText().toString().isEmpty())
                            return;

                        String text = messageEditText.getText().toString();
                        messageEditText.getText().clear();
                        sendMessage(user_id,text);
                    }
                });
                
            }
        });
    }

    public void sendMessage(final long user_id, final String text){
        GestorDB.getGesorDB().sendChatMessage(this, user_id, text, new DBResultCallBack() {
            @Override
            public void onGetResult(String result) {
                Date datetime = Calendar.getInstance().getTime();
                Usuario usuario = GestorUsuarios.getGestorUsuarios().getCurrentUser();
                ChatMessage chatMessage = new ChatMessage(usuario, text, datetime);
                usuario.addChatMessage(user_id,chatMessage);
                adapter.refresh();
            }
        });
    }
}

class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatViewHolder>{

    private ChatMessage[] messages;
    private long user_id;

    public ChatRecyclerAdapter(long user_id) {
        this.user_id = user_id;
        messages = GestorUsuarios.getGestorUsuarios().getCurrentUser().getMessages(user_id);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutMensaje = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message, null);
        return new ChatViewHolder(elLayoutMensaje);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = messages[position];
        holder.fechaMensaje.setText(message.getDate());
        holder.nombreUsuario.setText(message.getUsuario().getName());
        holder.mensajeChat.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.length;
    }

    public void refresh(){
        messages = GestorUsuarios.getGestorUsuarios().getCurrentUser().getMessages(user_id);
        notifyDataSetChanged();
    }

}

class ChatViewHolder extends RecyclerView.ViewHolder{

    public TextView nombreUsuario;
    public TextView mensajeChat;
    public TextView fechaMensaje;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        nombreUsuario = itemView.findViewById(R.id.nombreMensaje);
        mensajeChat = itemView.findViewById(R.id.mensajeChat);
        fechaMensaje = itemView.findViewById(R.id.fechaMensaje);
    }
}

class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}