package com.example.seguimientonutricional;

import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile {

  private String id;
  private String name;
  private String firstLastName;
  private String secondLastName;
  private String email;
  private float altura;
  private float circunferencia;
  private float peso;
  private String photoUrl;

  public Profile() {

  }

  public void setId(String id) { this.id = id; }

  public void setName(String name, String firstLastName, String secondLastName) {
    this.name = Objects.toString(name, "");
    this.firstLastName = Objects.toString(firstLastName, "");
    this.secondLastName = Objects.toString(secondLastName, "");
  }

  public void setEmail(String email) { this.email = email; }

  public void setAltura(float altura) { this.altura = altura; }

  public void setCircunferencia(float circunferencia) { this.circunferencia = circunferencia; }

  public void setPeso(float peso) { this.peso = peso; }

  public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

  public String getId() { return id; }

  public String getName() { return name; }

  public String getFirstLastName() { return firstLastName; }

  public String getSecondLastName() { return secondLastName; }

  public String getEmail() { return email; }

  public Float getAltura() { return altura; }

  public Float getCircunferencia() { return circunferencia; }

  public Float getPeso() { return peso; }

  public String getPhotoUrl() { return photoUrl; }

}
