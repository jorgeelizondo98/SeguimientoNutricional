package com.example.seguimientonutricional;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Profile {

  private String name;
  private String firstLastName;
  private String secondLastName;
  private String email;
  private float altura;
  private float circunferencia;
  private float peso;

  public Profile() {}

  public Profile(FirebaseUser user) {
    // TODO: Definir como manejaremos el nombre, si nos traemos el display name del usuario, o
    //    guardamos nosotros un nombre para el usuario.
    String raw_name = user.getDisplayName();
    name = raw_name.substring(0, raw_name.indexOf(' '));
    email = user.getEmail();
  }

  public void setName(String name, String firstLastName, String secondLastName) {
    this.name = Objects.toString(name, "");
    this.firstLastName = Objects.toString(firstLastName, "");
    this.secondLastName = Objects.toString(secondLastName, "");
  }

  public void setEmail(String email) { this.email = email; }

  public void setAltura(float altura) { this.altura = altura; }

  public void setCircunferencia(float circunferencia) { this.circunferencia = circunferencia; }

  public void setPeso(float peso) { this.peso = peso; }

  public String getName() { return name; }

  public String getFirstLastName() { return firstLastName; }

  public String getSecondLastName() { return secondLastName; }

  public String getEmail() { return email; }

  public Float getAltura() { return altura; }

  public Float getCircunferencia() { return circunferencia; }

  public Float getPeso() { return peso; }

}
