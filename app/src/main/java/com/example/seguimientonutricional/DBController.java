package com.example.seguimientonutricional;

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
  private final static String PROFILE_ALTURA = "altura";
  private final static String PROFILE_CIRCUNFERENCIA = "circunferencia";
  private final static String PROFILE_PESO = "peso";
  private final static String PROFILE_PHOTOURL = "fotoPerfil";

  public DBController() {
    db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();
    db.setFirestoreSettings(settings);
    storage = FirebaseStorage.getInstance();
  }

  private Map<String, Object> formatProfile(Profile profile) {
    Map<String, Object> formatted_profile = new HashMap<>();
    formatted_profile.put(PROFILE_NAME, profile.getName());
    formatted_profile.put(PROFILE_FIRSTLASTNAME, Objects.toString(profile.getFirstLastName(), ""));
    formatted_profile.put(PROFILE_SECONDLASTNAME, Objects.toString(profile.getSecondLastName(), ""));
    formatted_profile.put(PROFILE_EMAIL, profile.getEmail());
    formatted_profile.put(PROFILE_ALTURA, profile.getAltura());
    formatted_profile.put(PROFILE_CIRCUNFERENCIA, profile.getCircunferencia());
    formatted_profile.put(PROFILE_PESO, profile.getPeso());
    formatted_profile.put(PROFILE_PHOTOURL, profile.getPhotoUrl());
    return formatted_profile;
  }

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
              profile[0].setAltura(((Double) raw_profile.get(PROFILE_ALTURA)).floatValue());
              profile[0].setCircunferencia(((Double) raw_profile.get(PROFILE_CIRCUNFERENCIA)).floatValue());
              profile[0].setPeso(((Double) raw_profile.get(PROFILE_PESO)).floatValue());
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

  public void updateProfile(Profile profile) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).set(formatProfile(profile),
        SetOptions.merge());
  }

}
