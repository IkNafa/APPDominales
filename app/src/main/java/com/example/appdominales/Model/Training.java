package com.example.appdominales.Model;

import java.util.HashMap;

public class Training {
    private long id;
    private Usuario client;
    private Usuario owner;
    private String name;
    private HashMap<Long, Routine> routines;

    public Training(long pId, String pName, Usuario pOwner){
        id = pId;
        name = pName;
        owner = pOwner;
        routines = new HashMap<>();
    }

    public Training(long pId, String pName, Usuario pOwner, Usuario pClient){
        this(pId,pName,pOwner);
        client = pClient;
    }

    public Usuario getClient() {
        return client;
    }

    public Usuario getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void addRoutine(Routine routine){
        routines.put(routine.getDay(), routine);
    }

    public Routine getRoutine(long dayIndex){
        return routines.get(dayIndex);
    }

    public Routine[] getRoutineList(){
        return routines.values().toArray(new Routine[0]);
    }
}
