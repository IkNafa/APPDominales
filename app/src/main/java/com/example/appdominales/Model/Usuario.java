package com.example.appdominales.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class Usuario {

    private final long id;
    private final String name;
    private String email;
    private String image;
    private String provider;
    private String function = "";
    private String phone = "";
    private boolean is_trainer = false;
    private String genero = "";
    private String birthday = "";
    private boolean loaded = false;
    private HashMap<Long, Training> trainings;
    private HashMap<Long, Measure> measures;
    private Measure currentMeasure;
    private ArrayList<UserGoal> goals;
    private ArrayList<String> tags;
    private Usuario trainer;
    private HashMap<Long, Usuario> clients;
    private HashMap<Long, ArrayList<ChatMessage>> chats;

    private long client_count;
    private double rating_mean;

    public Usuario(long pId, String pName){
        id = pId;
        name = pName;
        trainings = new HashMap<>();
        measures = new HashMap<>();
        goals = new ArrayList<>();
        tags = new ArrayList<>();
        clients = new HashMap<>();
        chats = new HashMap<>();
    }

    public Usuario(long pId, String pName, String pEmail){
        this(pId, pName);
        email = pEmail;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGenero() {
        return genero;
    }

    public void setBirthday(String birthday){
        this.birthday = birthday;
    }

    public String getBirthday(){
        return birthday;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean is_trainer() {
        return is_trainer;
    }

    public void setIs_trainer(boolean is_trainer) {
        this.is_trainer = is_trainer;
    }

    public void addTraining(Training pTraining){
        trainings.put(pTraining.getId(), pTraining);
    }

    public Training[] getTrainingArray(){
        return trainings.values().toArray(new Training[0]);
    }

    public Training getTraining(long training_id){
        return trainings.get(training_id);
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public Usuario getTrainer() {
        return trainer;
    }

    public void setTrainer(Usuario trainer) {
        this.trainer = trainer;
    }

    public long getClient_count() {
        return client_count;
    }

    public void setClient_count(long client_count) {
        this.client_count = client_count;
    }

    public double getRating_mean() {
        return rating_mean;
    }

    public void setRating_mean(double rating_mean) {
        this.rating_mean = rating_mean;
    }

    public void addGoal(UserGoal pGoal){
        goals.add(pGoal);
    }

    public UserGoal[] getGoals(){
        return goals.toArray(new UserGoal[0]);
    }

    public Measure getMeasure(long id){
        return measures.get(id);
    }

    public void setCurrentMeasure(Measure measure) {
        currentMeasure = measure;
        addMeasure(measure);
    }

    public Measure getCurrentMeasure() {
        return currentMeasure;
    }

    public void addMeasure(Measure measure){
        measures.put(measure.getId(), measure);
    }

    public void clearMeasures(){
        measures.clear();
    }

    public void addTag(String tag){
        tags.add(tag);
    }

    public String[] getTags(){
        return tags.toArray(new String[0]);
    }

    public void clearTags(){
        tags.clear();
    }

    public void addClient(Usuario usuario){
        clients.put(usuario.getId(),usuario);
        client_count = clients.size();
    }

    public Usuario getClient(long pId){
        return clients.get(pId);
    }

    public void clearGoals() {
        goals.clear();
    }

    public void addChatMessage(long user_id,ChatMessage message){
        if(chats.get(user_id) == null){
            chats.put(user_id, new ArrayList<ChatMessage>());
        }
        chats.get(user_id).add(0,message);
    }

    public ChatMessage[] getMessages(long user_id){
        ArrayList<ChatMessage> messages = chats.get(user_id);
        if(messages == null) {
            return new ChatMessage[0];
        }else{
            return messages.toArray(new ChatMessage[0]);
        }
    }

    public void clearMessages(long user_id) {
        if(chats.get(user_id) != null){
            chats.get(user_id).clear();
        }
    }
}
