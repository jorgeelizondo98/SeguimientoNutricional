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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DBController extends Profile {

  FirebaseFirestore db;
  private FirebaseUser user;
  private String id;

  private final static String TAG = "database";

  private final static String COLLECTION_PROFILE = "Pacientes";
  private final static String PROFILE_NAME = "nombre";
  private final static String PROFILE_FIRSTLASTNAME = "apellidoPaterno";
  private final static String PROFILE_SECONDLASTNAME = "apellidoMaterno";
  private final static String PROFILE_EMAIL = "correo";
  private final static String PROFILE_ALTURA = "altura";
  private final static String PROFILE_CIRCUNFERENCIA = "circunferencia";
  private final static String PROFILE_PESO = "peso";

  public DBController(FirebaseUser user) {
    this.user = user;
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
    return formatted_profile;
  }

  public Profile getProfile() {
    db = FirebaseFirestore.getInstance();

    final Profile[] profile = { new Profile(user) };
    // Search in the database for the profile.
    db.collection(COLLECTION_PROFILE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
      @Override
      public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
          for (QueryDocumentSnapshot document : task.getResult()) {
            Log.d(TAG, document.getId() + " => " + document.getData());
            Map<String, Object> raw_profile = document.getData();
            if ((String) raw_profile.get(PROFILE_EMAIL) == user.getEmail()) {
              // The profile was found. Format it.
              DBController.this.id = document.getId();
              profile[0].setName((String) raw_profile.get(PROFILE_NAME),
                  (String) raw_profile.get(PROFILE_FIRSTLASTNAME),
                  (String) raw_profile.get(PROFILE_SECONDLASTNAME));
              profile[0].setEmail((String) raw_profile.get(PROFILE_EMAIL));
              profile[0].setAltura((Float) raw_profile.get(PROFILE_ALTURA));
              profile[0].setCircunferencia((Float) raw_profile.get(PROFILE_CIRCUNFERENCIA));
              profile[0].setPeso((Float) raw_profile.get(PROFILE_PESO));
              return;
            }
          }
          // The profile does not exist, so it should be created.
          db.collection(COLLECTION_PROFILE)
              .add(formatProfile(profile[0]))
              .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                  Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                  DBController.this.id = documentReference.getId();
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
    db.collection(COLLECTION_PROFILE).document(id).set(formatProfile(profile), SetOptions.merge());
  }

}
