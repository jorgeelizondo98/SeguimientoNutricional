package com.example.seguimientonutricional;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

public class DBController {

  private FirebaseFirestore db;
  private FirebaseStorage storage;

  private DBResponseListener dbResponseListener;

  private final static String TAG = "database";

  private final static String COLLECTION_DOCTORES = "Doctores";
  private final static String DOCTOR_PACIENTE_REF = "ref";

  private final static String COLLECTION_PROFILE = "Pacientes";
  private final static String PROFILE_NAME = "nombre";
  private final static String PROFILE_FIRSTLASTNAME = "apellidoPaterno";
  private final static String PROFILE_SECONDLASTNAME = "apellidoMaterno";
  private final static String PROFILE_EMAIL = "correo";
  private final static String PROFILE_PHOTOURL = "fotoPerfil";

  private final static String COLLECTION_ACTIVIDADES = "actividades";
  private final static String REGISTRO_TITULO = "titulo";
  private final static String REGISTRO_DESCRIPCION = "descripcion";
  private final static String REGISTRO_FECHA = "fecha";
  private final static String REGISTRO_COMENTARIO = "comentario";
  private final static String REGISTRO_TIPO = "tipo";

  private final static String COMIDA_FOTO = "foto";
  private final static String COMIDA_TIPO = "comida";
  private final static String BEBIDA_TIPO = "bebida";
  private final static String EJERCICIO_TIPO = "ejercicio";

  // Constructor initializes database and storage classes.
  public DBController(Object object) {
    db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();
    db.setFirestoreSettings(settings);
    storage = FirebaseStorage.getInstance();
    dbResponseListener = (DBResponseListener) object;
  }

  public interface DBResponseListener {
    void onDatabaseNetworkError();
    void onProfileReceived(Profile profile) throws ParseException;
    void onComidasReceived(ArrayList<Comida> comidas);
    void onBebidasReceived(ArrayList<Bebida> bebidas);
    void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios);
  }

  // Takes an instance of Profile and formats it into a map of <String, Object>.
  private Map<String, Object> formatProfile(Profile profile) {
    Map<String, Object> formatted_profile = new HashMap<>();
    formatted_profile.put(PROFILE_NAME, profile.getName());
    formatted_profile.put(PROFILE_FIRSTLASTNAME, Objects.toString(profile.getFirstLastName(), ""));
    formatted_profile.put(PROFILE_SECONDLASTNAME, Objects.toString(profile.getSecondLastName(), ""));
    formatted_profile.put(PROFILE_EMAIL, profile.getEmail());
    formatted_profile.put(PROFILE_PHOTOURL, profile.getPhotoUrl());
    return formatted_profile;
  }

  public void loadProfile(final FirebaseUser user) {
    // Search in the database for the profile.
    db.collection(COLLECTION_PROFILE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        final Profile[] profile = {new Profile()};
        if (task.isSuccessful()) {
          for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());
            Map<String, Object> raw_profile = document.getData();
            if (user.getEmail().equals(raw_profile.get(PROFILE_EMAIL))) {
              // The profile was found. Format it.
              profile[0].setId(document.getId());
              profile[0].setName((String) raw_profile.get(PROFILE_NAME),
                  (String) raw_profile.get(PROFILE_FIRSTLASTNAME),
                  (String) raw_profile.get(PROFILE_SECONDLASTNAME));
              profile[0].setEmail((String) raw_profile.get(PROFILE_EMAIL));
              profile[0].setPhotoUrl((String) raw_profile.get(PROFILE_PHOTOURL));
              try {
                dbResponseListener.onProfileReceived(profile[0]);
              } catch (ParseException e) {
                e.printStackTrace();
              }
              return;
            }
          }
          // The profile does not exist, so it should be created.
          profile[0].setName(
              (user.getDisplayName() != null? user.getDisplayName() : null),
              null, null);
          profile[0].setEmail(user.getEmail());
          profile[0].setPhotoUrl((user.getPhotoUrl() != null? user.getPhotoUrl().toString() : null));
          db.collection(COLLECTION_PROFILE)
              .add(formatProfile(profile[0]))
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                  Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                  profile[0].setId(documentReference.getId());
                  try {
                    dbResponseListener.onProfileReceived(profile[0]);
                  } catch (ParseException e) {
                    e.printStackTrace();
                  }
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.w(TAG, "Error adding document", e);
                  dbResponseListener.onDatabaseNetworkError();
                }
              });
        } else {
          Log.d(TAG, "Error getting documents: ", task.getException());
          dbResponseListener.onDatabaseNetworkError();
        }
      }
    });
  }

  public void updateProfile(Profile profile) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).set(formatProfile(profile),
        SetOptions.merge());
  }

  private Map<String, Object> formatRegistro(Registro registro, Map<String, Object> formatted_registro) {
    formatted_registro.put(REGISTRO_TITULO, registro.getTitulo());
    formatted_registro.put(REGISTRO_DESCRIPCION, registro.getDescripcion());
    formatted_registro.put(REGISTRO_FECHA, registro.getFecha());
    formatted_registro.put(REGISTRO_COMENTARIO, registro.getComentario());
    return formatted_registro;
  }

  // Takes an instance of Comida and formats it into a map of <String, Object>.
  private Map<String, Object> formatComida(Comida comida) {
    Map<String, Object> formatted_comida = new HashMap<>();
    formatted_comida.put(COMIDA_FOTO, comida.getFotoUrl());
    formatted_comida.put(REGISTRO_TIPO, COMIDA_TIPO);
    return formatRegistro(comida, formatted_comida);
  }

  // Takes an instance of Bebida and formats it into a map of <String, Object>.
  private Map<String, Object> formatBebida(Bebida bebida) {
    Map<String, Object> formatted_bebida = new HashMap<>();
    formatted_bebida.put(REGISTRO_TIPO, BEBIDA_TIPO);
    return formatRegistro(bebida, formatted_bebida);
  }

  // Takes an instance of Ejercicio and formats it into a map of <String, Object>.
  private Map<String, Object> formatEjercicio(Ejercicio ejercicio) {
    Map<String, Object> formatted_ejercicio = new HashMap<>();
    formatted_ejercicio.put(REGISTRO_TIPO, EJERCICIO_TIPO);
    return formatRegistro(ejercicio, formatted_ejercicio);
  }

  public void addComida(final Profile profile, final Comida comida) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).collection(COLLECTION_ACTIVIDADES)
        .add(formatComida(comida))
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onComplete(@NonNull Task<DocumentReference> task) {
            loadComidas(profile, comida.getFecha());
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            dbResponseListener.onDatabaseNetworkError();
          }
        });
  }

  public void addBebida(final Profile profile, final Bebida bebida) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).collection(COLLECTION_ACTIVIDADES)
        .add(formatBebida(bebida))
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onComplete(@NonNull Task<DocumentReference> task) {
            loadBebidas(profile, bebida.getFecha());
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            dbResponseListener.onDatabaseNetworkError();
          }
        });
  }

  public void addEjercicio(final Profile profile, final Ejercicio ejercicio) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).collection(COLLECTION_ACTIVIDADES)
        .add(formatEjercicio(ejercicio))
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onComplete(@NonNull Task<DocumentReference> task) {
            loadEjercicios(profile, ejercicio.getFecha());
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            dbResponseListener.onDatabaseNetworkError();
          }
        });
  }

  // Takes a Comida and updates it in the databse.
  public void updateComida(Profile profile, Comida comida) {
    db.collection(COLLECTION_PROFILE).document(profile.getId())
        .collection(COLLECTION_ACTIVIDADES).document(comida.getId())
            .set(formatComida(comida), SetOptions.merge());
  }

  // Takes a Bebida and updates it in the databse.
  public void updateBebida(Profile profile, Bebida bebida) {
    db.collection(COLLECTION_PROFILE).document(profile.getId())
        .collection(COLLECTION_ACTIVIDADES).document(bebida.getId())
            .set(formatBebida(bebida), SetOptions.merge());
  }

  // Takes a ejercicio and updates it in the databse.
  public void updateEjercicio(Profile profile, Ejercicio ejercicio) {
    db.collection(COLLECTION_PROFILE).document(profile.getId())
        .collection(COLLECTION_ACTIVIDADES).document(ejercicio.getId())
            .set(formatEjercicio(ejercicio), SetOptions.merge());
  }

  // Takes a Profile, a Comida, and a Bitmap. uploads the Bitmap to Firebase Storage, gets the url
  //    and adds it to Comida.
  // If error, returned comida will have no id.
  public Comida uploadPhotoAndUpdateComida(final Profile profile, final Comida comida, Bitmap img) {
    final StorageReference ref = storage.getReference().child(profile.getId() + "/" + comida.getId());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = ref.putBytes(data);
    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        comida.setFotoUrl(ref.getDownloadUrl().toString());
        updateComida(profile, comida);
      }
    });
    return comida;
  }

  // Receives a type and a date, and returns the registers for that type in that date.
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void loadRegistros(String profileId, final String tipo, final Date date) {
    final PriorityQueue<QueryDocumentSnapshot> registros =
        new PriorityQueue<>(new Comparator<QueryDocumentSnapshot>() {
          @Override
          public int compare(QueryDocumentSnapshot o1, QueryDocumentSnapshot o2) {
            return ((Timestamp) o1.getData().get(REGISTRO_FECHA)).compareTo(
                (Timestamp) o2.getData().get(REGISTRO_FECHA));
          }
        });
    db.collection(COLLECTION_PROFILE).document(profileId).collection(COLLECTION_ACTIVIDADES)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            for (QueryDocumentSnapshot document : task.getResult()) {
              Log.d(TAG, document.getId() + " => " + document.getData());
              Date registroDate = ((Timestamp) document.getData().get(REGISTRO_FECHA)).toDate();
              if (tipo.equals(document.getData().get(REGISTRO_TIPO)) &&
                  sdf.format(date).equals(sdf.format(registroDate))) {
                registros.add(document);
              }
            }
            switch (tipo) {
              case COMIDA_TIPO:
                DBController.this.onRawComidasReceived(registros);
                break;
              case BEBIDA_TIPO:
                DBController.this.onRawBebidasReceived(registros);
                break;
              case EJERCICIO_TIPO:
                DBController.this.onRawEjercicioReceived(registros);
                break;
            }
          } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
            dbResponseListener.onDatabaseNetworkError();
          }
        }
      });
  }

  private Registro populateRegistro(QueryDocumentSnapshot document) {
    Registro registro = new Registro();
    registro.setId(document.getId());
    Map<String, Object> rawRegistro = document.getData();
    registro.setTitulo((String) rawRegistro.get(REGISTRO_TITULO));
    registro.setDescripcion((String) rawRegistro.get(REGISTRO_DESCRIPCION));
    registro.setComentario((String) rawRegistro.get(REGISTRO_COMENTARIO));
    registro.setFecha(((Timestamp) document.get(REGISTRO_FECHA)).toDate());
    return registro;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadComidas(Profile profile, Date date) {
    loadRegistros(profile.getId(), COMIDA_TIPO, date);
  }

  private void onRawComidasReceived(PriorityQueue<QueryDocumentSnapshot> rawComidas) {
    ArrayList<Comida> comidas = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawComidas) {
      Comida comida = new Comida(populateRegistro(document));
      comida.setFotoUrl((String) document.getData().get(COMIDA_FOTO));
      comidas.add(comida);
    }
    dbResponseListener.onComidasReceived(comidas);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadBebidas(Profile profile, Date date) {
    loadRegistros(profile.getId(), BEBIDA_TIPO, date);
  }

  private void onRawBebidasReceived(PriorityQueue<QueryDocumentSnapshot> rawBebidas) {
    ArrayList<Bebida> bebidas = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawBebidas) {
      Bebida bebida = new Bebida(populateRegistro(document));
      bebidas.add(bebida);
    }
    dbResponseListener.onBebidasReceived(bebidas);
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadEjercicios(Profile profile, Date date) {
    loadRegistros(profile.getId(), EJERCICIO_TIPO, date);
  }

  private void onRawEjercicioReceived(PriorityQueue<QueryDocumentSnapshot> rawEjercicio) {
    ArrayList<Ejercicio> ejercicios = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawEjercicio) {
      Ejercicio ejercicio = new Ejercicio(populateRegistro(document));
      ejercicios.add(ejercicio);
    }
    dbResponseListener.onEjerciciosReceived(ejercicios);
  }

  public void associateDoctor(final Profile profile, final String doctorId) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).get().addOnCompleteListener(
        new OnCompleteListener<DocumentSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
          if (task.isSuccessful()) {
            Map<String, Object> reference = new HashMap<>();
            reference.put(DOCTOR_PACIENTE_REF, task.getResult().getReference());
            db.collection(COLLECTION_DOCTORES).document(doctorId).collection(COLLECTION_PROFILE)
                .document(profile.getId()).set(reference, SetOptions.merge());
          } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
            dbResponseListener.onDatabaseNetworkError();
          }
        }
      });
  }
}
