package com.example.appdominales.Model;

import com.example.appdominales.Controller.ExerciseSet;

import java.util.ArrayList;

public class Exercise {

    private String  name,
                    description,
                    external_video,
                    image,
                    tempo,
                    group,
                    range,
                    bartype,
                    grip,
                    stance;
    private ArrayList<ExerciseSet> sets;

    public Exercise(String name, String description, String external_video, String image, String tempo, String group, String range, String bartype, String grip, String stance) {
        this.name = name;
        this.description = description;
        this.external_video = external_video;
        this.image = image;
        this.tempo = tempo;
        this.group = group;
        this.range = range;
        this.bartype = bartype;
        this.grip = grip;
        this.stance = stance;

        sets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getExternal_video() {
        return external_video;
    }

    public String getImage() {
        return image;
    }

    public void addSet(ExerciseSet set){
        sets.add(set);
    }
}
