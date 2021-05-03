package com.example.appdominales.Model;

public class UserGoal {
    private final String name;
    private final String description;

    public UserGoal(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
