package com.example.appdominales.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {

    private Usuario usuario;
    private String message;
    private Date date;
    private String hourString;

    public ChatMessage(Usuario usuario, String message, String date) {
        this.usuario = usuario;
        this.message = message;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            this.date = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public ChatMessage(Usuario usuario, String message, Date date) {
        this.usuario = usuario;
        this.message = message;
        this.date = date;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }
}
