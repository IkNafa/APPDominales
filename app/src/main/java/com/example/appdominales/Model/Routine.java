package com.example.appdominales.Model;

import java.util.ArrayList;

public class Routine {

    private long id;
    private String name;
    private long day;
    private long exercise_count;
    private ArrayList<Exercise> exercises;
    private String dayString;

    public Routine(long id, String name, long day, long exercise_count) {
        this.id = id;
        this.name = name;
        this.day = day;
        this.exercise_count = exercise_count;
        exercises = new ArrayList<>();

        switch ((int) day){
            case 0:
                dayString="LUN";
                break;
            case 1:
                dayString="MAR";
                break;
            case 2:
                dayString="MIER";
                break;
            case 3:
                dayString="JUE";
                break;
            case 4:
                dayString="VIE";
                break;
            case 5:
                dayString="SAB";
                break;
            case 6:
                dayString="DOM";
                break;
        }
    }

    public String getName() {
        return name;
    }

    public long getDay() {
        return day;
    }

    public long getExercise_count() {
        return exercise_count;
    }

    public void addExercise(Exercise exercise){
        exercises.add(exercise);
        exercise_count = exercises.size();
    }

    public String getDayString() {
        return dayString;
    }

    public Exercise[] getExerciseList(){
        return exercises.toArray(new Exercise[0]);
    }
}
