package com.example.seguimientonutricional.test;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.Profile;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DBController_test implements DBController.DBResponseListener {

  private DBController db;
  private SimpleDateFormat sdf;
  private Profile profile;

  private final static String TAG = "test";

  @RequiresApi(api = Build.VERSION_CODES.N)
  public DBController_test(FirebaseUser user) {
    db = new DBController(this);
    sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    loadProfile_test(user);
  }

  private void loadProfile_test(FirebaseUser user) {
    db.loadProfile(user);
  }

  private void updateProfile_test() {
    db.updateProfile(profile);
  }

  private void addComida_test() throws ParseException {
    Comida comida1 = new Comida();
    Comida comida2 = new Comida();
    Comida comida3 = new Comida();
    Comida comida4 = new Comida();

    comida1.setTitulo("Tacos de picadillo");
    comida1.setDescripcion("5 Tacos de picadillo");
    comida1.setFecha(sdf.parse("2020/05/10 14:30:20"));
    db.addComida(profile, comida1);

    comida2.setTitulo("Ensalada de pollo");
    comida2.setDescripcion("Medio litro de ensala de pollo");
    comida2.setFecha(sdf.parse("2020/05/11 15:30:20"));
    db.addComida(profile, comida2);

    comida3.setTitulo("Pollo a la plancha");
    comida3.setDescripcion("300 gr de pechuga de pollo a la plancha");
    comida3.setFecha(sdf.parse("2020/05/11 16:30:20"));
    db.addComida(profile, comida3);

    comida4.setTitulo("Verduras al vapor");
    comida4.setDescripcion("500 gr de verduras al vapor");
    comida4.setFecha(sdf.parse("2020/05/12 18:30:20"));
    db.addComida(profile, comida4);
  }

  private void addBebida_test() throws ParseException {
    Bebida bebida1 = new Bebida();

    bebida1.setTitulo("Agua");
    bebida1.setDescripcion("500 ml agua");
    bebida1.setFecha(sdf.parse("2020/05/10 14:30:20"));
    db.addBebida(profile, bebida1);
  }

  private void addEjercicio_test() throws ParseException {
    Ejercicio ejercicio1 = new Ejercicio();

    ejercicio1.setTitulo("Sentadillas");
    ejercicio1.setDescripcion("5 sets de 10 sentadillas");
    ejercicio1.setFecha(sdf.parse("2020/05/10 14:30:20"));
    db.addEjercicio(profile, ejercicio1);
  }

  @Override
  public void onDatabaseNetworkError() {
    Log.d(TAG, "Error de conexión");
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  @Override
  public void onProfileReceived(Profile profile) throws ParseException {
    this.profile = profile;
    db.associateDoctor(profile, "ypghi03Et7bUnvJ8mxOqj2Zudi52");
//    addComida_test();
//    addBebida_test();
//    addEjercicio_test();
//    db.loadComidas(profile, sdf.parse("2020/05/11 00:00:00"));
//    db.loadBebidas(profile, sdf.parse("2020/05/10 00:00:00"));
//    db.loadEjercicios(profile, sdf.parse("2020/05/10 00:00:00"));
  }

  @Override
  public void onComidasReceived(ArrayList<Comida> comidas) {
    for (Comida comida : comidas) {
      Log.d(TAG, "\nComida **********\n" + comida.getTitulo() + '\n' + comida.getDescripcion() + '\n' + comida.getFecha() + "\n**********");
    }
  }

  @Override
  public void onBebidasReceived(ArrayList<Bebida> bebidas) {
    for (Bebida bebida : bebidas) {
      Log.d(TAG, "\nBebida **********\n" + bebida.getTitulo() + '\n' + bebida.getDescripcion() + '\n' + bebida.getFecha() + "\n**********");
    }
  }

  @Override
  public void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios) {
    for (Ejercicio ejercicio : ejercicios) {
      Log.d(TAG, "\nEjercicio **********\n" + ejercicio.getTitulo() + '\n' + ejercicio.getDescripcion() + '\n' + ejercicio.getFecha() + "\n**********");
    }
  }

  @Override
  public void onNewDoctorAssociated(Profile profile) {

  }

  @Override
  public void onComidaPhotoAdded(Comida comida) {

  }
}
