package com.example.appdominales.Model;

public class Measure {

    private final long id;
    private final double weight;
    private final long height;
    private final String date;

    private float porGrasa;
    private float masaMuscular;
    private float contornoBrazo;
    private float contornoCintura;
    private float contornoMuslo;
    private float contornoCuello;
    private float contornoPecho;
    private float contornoGemelo;

    public Measure(long id,String date, double weight, long height) {
        this.id = id;
        this.weight = weight;
        this.height = height;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public double getWeight() {
        return weight;
    }

    public long getHeight() {
        return height;
    }

    public String getDate() {
        return date;
    }

    public float getPorGrasa() {
        return porGrasa;
    }

    public void setPorGrasa(float porGrasa) {
        this.porGrasa = porGrasa;
    }

    public float getMasaMuscular() {
        return masaMuscular;
    }

    public void setMasaMuscular(float masaMuscular) {
        this.masaMuscular = masaMuscular;
    }

    public float getContornoBrazo() {
        return contornoBrazo;
    }

    public void setContornoBrazo(float contornoBrazo) {
        this.contornoBrazo = contornoBrazo;
    }

    public float getContornoCintura() {
        return contornoCintura;
    }

    public void setContornoCintura(float contornoCintura) {
        this.contornoCintura = contornoCintura;
    }

    public float getContornoMuslo() {
        return contornoMuslo;
    }

    public void setContornoMuslo(float contornoMuslo) {
        this.contornoMuslo = contornoMuslo;
    }

    public float getContornoCuello() {
        return contornoCuello;
    }

    public void setContornoCuello(float contornoCuello) {
        this.contornoCuello = contornoCuello;
    }

    public float getContornoPecho() {
        return contornoPecho;
    }

    public void setContornoPecho(float contornoPecho) {
        this.contornoPecho = contornoPecho;
    }

    public float getContornoGemelo() {
        return contornoGemelo;
    }

    public void setContornoGemelo(float contornoGemelo) {
        this.contornoGemelo = contornoGemelo;
    }
}
