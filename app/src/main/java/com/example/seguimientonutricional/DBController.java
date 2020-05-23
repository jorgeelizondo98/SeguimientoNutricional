package com.example.seguimientonutricional;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DBController {

  FirebaseFirestore db;
  FirebaseStorage storage;

  private final static String TAG = "database";

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

  // Constructor initializes database and storage classes.
  public DBController() {
    db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();
    db.setFirestoreSettings(settings);
    storage = FirebaseStorage.getInstance();
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

  // Takes a FirebaseUser and retrieves the profile from the database, or creates it if not existent.
  public Profile getProfile(final FirebaseUser user) {
    final Profile[] profile = { new Profile() };
    // Search in the database for the profile.
    db.collection(COLLECTION_PROFILE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
              return;
            }
          }
          // The profile does not exist, so it should be created.
          profile[0].setName(
              user.getDisplayName().substring(0, user.getDisplayName().indexOf(' ')),
              null, null);
          profile[0].setEmail(user.getEmail());
          profile[0].setPhotoUrl(user.getPhotoUrl().toString());
          db.collection(COLLECTION_PROFILE)
              .add(formatProfile(profile[0]))
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                  Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                  profile[0].setId(documentReference.getId());
                }
              })
              .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.w(TAG, "Error adding document", e);
                  profile[0] = null;
                }
              });
        } else {
          Log.d(TAG, "Error getting documents: ", task.getException());
          profile[0] = null;
        }
      }
    });
    return profile[0];
  }

  // Takes a Profile and updates it in the database.
  public boolean updateProfile(Profile profile) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).set(formatProfile(profile),
        SetOptions.merge());
    return true;
  }

  // Takes an instance of Registro and formats it into a map of <String, Object>.
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

  // Takes a Comida and adds it to the databse.
  // If error returns false.
  public boolean addComida(Comida comida) {
    final boolean[] success = {true};
    db.collection(COLLECTION_ACTIVIDADES)
        .add(formatComida(comida))
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.w(TAG, "Error adding document", e);
            success[0] = false;
          }
        });
    return success[0];
  }

  // Takes a Comida and updates it in the databse.
  // If error returns false.
  public boolean updateComida(Comida comida) {
    db.collection(COLLECTION_ACTIVIDADES).document(comida.getId()).set(formatComida(comida),
        SetOptions.merge());
    return true;
  }

  // Takes a Profile, a Comida, and a Bitmap. uploads the Bitmap to Firebase Storage, gets the url
  //    and adds it to Comida.
  // If error, returned comida will have no id.
  public Comida uploadPhotoAndUpdateComida(Profile profile, final Comida comida, Bitmap img) {
    final StorageReference ref = storage.getReference().child(profile.getId() + "/" + comida.getId());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = ref.putBytes(data);
    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        comida.setFotoUrl(ref.getDownloadUrl().toString());
        updateComida(comida);
      }
    });
    return comida;
  }
}
