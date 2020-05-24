package com.example.seguimientonutricional.test;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.Profile;
import com.example.seguimientonutricional.Registro;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBController_test {

  private DBController db;
  private SimpleDateFormat sdf;

  @RequiresApi(api = Build.VERSION_CODES.N)
  public DBController_test(FirebaseUser user) throws ParseException {
    db = new DBController();
    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    Profile profile =  getProfile_test(user);
    updateProfile_test(profile);
    addThenUpdateComida_test();
    addThenUpdateBebida_test();
    addThenUpdateEjercicio_test();
    getComidas_test();
    getBebidas_test();
    getEjercicios_test();
  }

  private Profile getProfile_test(FirebaseUser user) {
    Profile profile = db.getProfile(user);

    assert !profile.getEmail().isEmpty();
    assert !profile.getName().isEmpty();
    assert !profile.getPhotoUrl().isEmpty();

    return profile;
  }

  private void updateProfile_test(Profile profile) {
    assert db.updateProfile(profile);
  }

  private void addThenUpdateComida_test() throws ParseException {
    Comida comida1 = new Comida();
    Comida comida2 = new Comida();
    Comida comida3 = new Comida();
    Comida comida4 = new Comida();

    comida1.setTitulo("Tacos de picadillo");
    comida1.setDescripcion("5 Tacos de picadillo");
    comida1.setFecha(sdf.parse("2020/05/10 14:30:20"));

    String id = db.addComida(comida1).getId();
    assert !id.isEmpty();
    comida1.setId(id);
    comida1.setTitulo("5 Tacos de picadillo");
    assert db.updateComida(comida1);

    comida2.setTitulo("Ensalada de pollo");
    comida2.setDescripcion("Medio litro de ensala de pollo");
    comida2.setFecha(sdf.parse("2020/05/11 15:30:20"));
    assert !db.addComida(comida2).getId().isEmpty();

    comida3.setTitulo("Pollo a la plancha");
    comida3.setDescripcion("300 gr de pechuga de pollo a la plancha");
    comida3.setFecha(sdf.parse("2020/05/11 16:30:20"));
    assert !db.addComida(comida3).getId().isEmpty();

    comida4.setTitulo("Verduras al vapor");
    comida4.setDescripcion("500 gr de verduras al vapor");
    comida4.setFecha(sdf.parse("2020/05/12 18:30:20"));
    assert !db.addComida(comida3).getId().isEmpty();
  }

  private void addThenUpdateBebida_test() throws ParseException {
    Bebida bebida1 = new Bebida();

    bebida1.setTitulo("Tacos de picadillo");
    bebida1.setDescripcion("5 Tacos de picadillo");
    bebida1.setFecha(sdf.parse("2020/05/10 14:30:20"));

    String id = db.addBebida(bebida1).getId();
    assert !id.isEmpty();
    bebida1.setId(id);
    bebida1.setTitulo("5 Tacos de picadillo");
    assert db.updateBebida(bebida1);
  }

  private void addThenUpdateEjercicio_test() throws ParseException {
    Ejercicio ejercicio1 = new Ejercicio();

    ejercicio1.setTitulo("Tacos de picadillo");
    ejercicio1.setDescripcion("5 Tacos de picadillo");
    ejercicio1.setFecha(sdf.parse("2020/05/10 14:30:20"));

    String id = db.addEjercicio(ejercicio1).getId();
    assert !id.isEmpty();
    ejercicio1.setId(id);
    ejercicio1.setTitulo("5 Tacos de picadillo");
    assert db.updateEjercicio(ejercicio1);
  }

  private boolean compareRegistros(Registro r1, Registro r2) {
    return r1.getTitulo().equals(r2.getTitulo()) &&
        r1.getDescripcion().equals(r2.getDescripcion()) &&
        r1.getFecha().equals(r2.getFecha());
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void getComidas_test() throws ParseException {
    ArrayList<Comida> comidas = db.getComidas(sdf.parse("2020/05/11 00:00:00"));

    Comida comida0 = new Comida();
    comida0.setTitulo("Ensalada de pollo");
    comida0.setDescripcion("Medio litro de ensala de pollo");
    comida0.setFecha(sdf.parse("2020/05/11 15:30:20"));

    Comida comida1 = new Comida();
    comida1.setTitulo("Pollo a la plancha");
    comida1.setDescripcion("300 gr de pechuga de pollo a la plancha");
    comida1.setFecha(sdf.parse("2020/05/11 16:30:20"));

    assert comidas.size() == 2;
    assert compareRegistros(comidas.get(0), comida0);
    assert compareRegistros(comidas.get(1), comida1);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void getBebidas_test() throws ParseException {
    ArrayList<Bebida> bebidas = db.getBebidas(sdf.parse("2020/05/10 00:00:00"));

    Bebida bebida0 = new Bebida();
    bebida0.setTitulo("5 Tacos de picadillo");
    bebida0.setDescripcion("5 Tacos de picadillo");
    bebida0.setFecha(sdf.parse("2020/05/10 14:30:20"));

    assert bebidas.size() == 1;
    assert compareRegistros(bebidas.get(0), bebida0);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  private void getEjercicios_test() throws ParseException {
    ArrayList<Ejercicio> ejercicios = db.getEjercicios(sdf.parse("2020/05/09 00:00:00"));

    assert ejercicios.size() == 1;
  }
}

