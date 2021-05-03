package com.example.appdominales.Controller;

import com.example.appdominales.Model.Training;
import com.example.appdominales.Model.Usuario;

import java.util.HashMap;

public class GestorUsuarios {
    private static GestorUsuarios mGestorUsuarios;
    private HashMap<Long,Usuario> usuarios;
    private Usuario currentUser;

    private GestorUsuarios(){
        usuarios = new HashMap<>();
    }

    public static GestorUsuarios getGestorUsuarios(){
        if(mGestorUsuarios==null){
            mGestorUsuarios = new GestorUsuarios();
        }
        return mGestorUsuarios;
    }

    public void addUser(Usuario pUsuario){
        usuarios.put(pUsuario.getId(),pUsuario);
    }

    public Usuario getUsuario(long pId){
        return usuarios.get(pId);
    }

    public void setCurrentUser(long pId){
        currentUser = getUsuario(pId);
    }

    public Usuario getCurrentUser(){
        return currentUser;
    }

    public void addTrainingToUser(long pId, Training pTraining){
        usuarios.get(pId).addTraining(pTraining);
    }
}
