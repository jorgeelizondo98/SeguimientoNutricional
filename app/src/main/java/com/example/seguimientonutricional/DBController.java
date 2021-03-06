package com.example.seguimientonutricional;

import android.graphics.Bitmap;
import android.net.Uri;
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
  private final static String DOCTOR_NOMBRE = "nombre";
  private final static String DOCTOR_PACIENTE_REF = "ref";

  private final static String COLLECTION_PROFILE = "Pacientes";
  private final static String PROFILE_NAME = "nombre";
  private final static String PROFILE_FIRSTLASTNAME = "apellidoPaterno";
  private final static String PROFILE_SECONDLASTNAME = "apellidoMaterno";
  private final static String PROFILE_EMAIL = "correo";
  private final static String PROFILE_PHOTOURL = "fotoPerfil";
  private final static String PROFILE_DOCTOR_REF = "doctorActual";
  private final static String PROFILE_NOMBREDOCTOR = "nombreDoctor";
  private final static String PROFILE_BIRTHDATE = "fechaNacimiento";

  private final static String COLLECTION_ACTIVIDADES = "actividades";
  private final static String REGISTRO_TITULO = "titulo";
  private final static String REGISTRO_DESCRIPCION = "descripcion";
  private final static String REGISTRO_FECHA = "fecha";
  private final static String REGISTRO_COMENTARIO = "comentarioDoctor";
  private final static String REGISTRO_TIPO = "tipo";

  private final static String COMIDA_FOTO = "foto";
  private final static String COMIDA_TIPO = "comida";
  private final static String COMIDA_CARBOHIDRATOS = "carbohidratos";
  private final static String COMIDA_PROTEINAS = "proteinas";
  private final static String COMIDA_GRASAS = "grasas";
  private final static String BEBIDA_TIPO = "bebida";
  private final static String BEBIDA_CANTIDAD = "cantidad";
  private final static String BEBIDA_SODIO = "sodio";
  private final static String BEBIDA_AZUCARES = "azucares";
  private final static String EJERCICIO_TIPO = "ejercicio";
  private final static String EJERCICIO_DURACION = "cantidad";
  private final static String EJERCICIO_INTENSIDAD = "intensidad";

  private String currentComidaId;

  // Constructor inicializa el objeto de conexión a Firebase Firestore y Firebase Storage.
  // También Asigna el DBResponseListener recibido como parametro.
  public DBController(Object object) {
    db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build();
    db.setFirestoreSettings(settings);
    storage = FirebaseStorage.getInstance();
    dbResponseListener = (DBResponseListener) object;
  }

  // Interfaz listener de respuestas de la base de datos.
  public interface DBResponseListener {
    // Avisa que no se pudo acceder a la base de datos.
    void onDatabaseNetworkError();

    // Avisa que se recibio un profile y lo envía.
    void onProfileReceived(Profile profile) throws ParseException;

    // Avisa que se recibió/actualizó una lista de comidas.
    void onComidasReceived(ArrayList<Comida> comidas);

    // Avisa que se recibió/actualizó una lista de bebidas.
    void onBebidasReceived(ArrayList<Bebida> bebidas);

    // Avisa que se recibió/actualizó una lista de ejercicios.
    void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios);

    // Avisa que se asoció un nuevo doctor al perfil del usuario.
    void onNewDoctorAssociated(Profile profile);

    // Avisa que se ha agregado una foto a una comida.
    void onComidaPhotoAdded(Comida comida);
  }

  // Takes an instance of Profile and formats it into a map of <String, Object>.
  private Map<String, Object> formatProfile(Profile profile) {
    Map<String, Object> formatted_profile = new HashMap<>();
    formatted_profile.put(PROFILE_NAME, profile.getName());
    formatted_profile.put(PROFILE_FIRSTLASTNAME, Objects.toString(profile.getFirstLastName(), ""));
    formatted_profile.put(PROFILE_SECONDLASTNAME, Objects.toString(profile.getSecondLastName(), ""));
    formatted_profile.put(PROFILE_EMAIL, profile.getEmail());
    formatted_profile.put(PROFILE_NOMBREDOCTOR, profile.getNombreDoctor());
    formatted_profile.put(PROFILE_PHOTOURL, profile.getPhotoUrl());
    formatted_profile.put(PROFILE_BIRTHDATE, new Date());
    return formatted_profile;
  }

  // Carga un profile de la base de datos a través de su identificador de usuario de Firebase.
  public void loadProfile(final FirebaseUser user) {
    // Busca el perfil en la base de datos.
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
              profile[0].setNombreDoctor((String) raw_profile.get(PROFILE_NOMBREDOCTOR));
              try {
                dbResponseListener.onProfileReceived(profile[0]);
              } catch (ParseException e) {
                e.printStackTrace();
              }
              return;
            }
          }
          // Si no se encontró el perfil se procede a crearlo.
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

  // Actualiza el perfil de usuario en la base de datos.
  public void updateProfile(Profile profile) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).set(formatProfile(profile),
        SetOptions.merge());
  }

  // Formatea un objeto de registro de actividad para subirlo a la base de datos.
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
    formatted_comida.put(COMIDA_PROTEINAS, comida.getProteinas());
    formatted_comida.put(COMIDA_CARBOHIDRATOS, (comida.getCarbohidratos()));
    formatted_comida.put(COMIDA_GRASAS, comida.getGrasas());
    formatted_comida.put(COMIDA_FOTO, comida.getFotoUrl());
    formatted_comida.put(REGISTRO_TIPO, COMIDA_TIPO);
    return formatRegistro(comida, formatted_comida);
  }

  // Takes an instance of Bebida and formats it into a map of <String, Object>.
  private Map<String, Object> formatBebida(Bebida bebida) {
    Map<String, Object> formatted_bebida = new HashMap<>();
    formatted_bebida.put(BEBIDA_AZUCARES, bebida.getAzucares());
    formatted_bebida.put(BEBIDA_CANTIDAD, bebida.getCantidad());
    formatted_bebida.put(BEBIDA_SODIO, bebida.getSodio());
    formatted_bebida.put(REGISTRO_TIPO, BEBIDA_TIPO);
    return formatRegistro(bebida, formatted_bebida);
  }

  // Takes an instance of Ejercicio and formats it into a map of <String, Object>.
  private Map<String, Object> formatEjercicio(Ejercicio ejercicio) {
    Map<String, Object> formatted_ejercicio = new HashMap<>();
    formatted_ejercicio.put(EJERCICIO_INTENSIDAD, ejercicio.getIntensidad());
    formatted_ejercicio.put(EJERCICIO_DURACION, ejercicio.getDuracion());
    formatted_ejercicio.put(REGISTRO_TIPO, EJERCICIO_TIPO);
    return formatRegistro(ejercicio, formatted_ejercicio);
  }

  // Agrega una comida a la base de datos.
  public String addComida(final Profile profile, final Comida comida) {
    db.collection(COLLECTION_PROFILE).document(profile.getId()).collection(COLLECTION_ACTIVIDADES)
        .add(formatComida(comida))
        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
          @RequiresApi(api = Build.VERSION_CODES.N)
          @Override
          public void onComplete(@NonNull Task<DocumentReference> task) {
            currentComidaId = task.getResult().getId().toString();
            loadComidas(profile, comida.getFecha());
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            dbResponseListener.onDatabaseNetworkError();
          }
        });

    return currentComidaId;
  }

  // Agrega una bebida a la base de datos.
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

  // Agrega un ejercicio a la base de datos.
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
  public void uploadPhotoAndUpdateComida(final Profile profile, final Comida comida, Bitmap img) {
    final StorageReference ref = storage.getReference().child(profile.getId() + "/" +
        COLLECTION_ACTIVIDADES + comida.getFecha().toString());

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = ref.putBytes(data);
    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override
          public void onSuccess(Uri uri) {
            comida.setFotoUrl(uri.toString());
            if (comida.getId() == null) {
              addComida(profile, comida);
            } else {
              updateComida(profile, comida);
            }
            dbResponseListener.onComidaPhotoAdded(comida);
          }
        });
      }
    });
  }

  // Receives a type and a date, and returns the registers for that type in that date.
  @RequiresApi(api = Build.VERSION_CODES.N)
  private void loadRegistros(String profileId, final String tipo, final Date date) {
    // Guardar resultados en un priority queue que ordene los registros por fecha.
    final PriorityQueue<QueryDocumentSnapshot> registros =
        new PriorityQueue<>(new Comparator<QueryDocumentSnapshot>() {
          @Override
          public int compare(QueryDocumentSnapshot o1, QueryDocumentSnapshot o2) {
            return ((Timestamp) o2.getData().get(REGISTRO_FECHA)).compareTo(
                (Timestamp) o1.getData().get(REGISTRO_FECHA));
          }
        });
    // Busca los registros en la base de datos.
    db.collection(COLLECTION_PROFILE).document(profileId).collection(COLLECTION_ACTIVIDADES)
        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
          if (task.isSuccessful()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            for (QueryDocumentSnapshot document : task.getResult()) {
              Date registroDate = ((Timestamp) document.getData().get(REGISTRO_FECHA)).toDate();
              if (tipo.equals(document.getData().get(REGISTRO_TIPO)) &&
                  sdf.format(date).equals(sdf.format(registroDate))) {
                registros.add(document);
              }
            }
            // De acuerdo al tipo de registro llama a la función que lo asignara al objeto adecuado.
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

  // Crea un objeto registro a partir de una lectura de la base de datos.
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

  // Pide carga de registros de comida a la base de datos.
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadComidas(Profile profile, Date date) {
    loadRegistros(profile.getId(), COMIDA_TIPO, date);
  }

  // Crea un objeto de comida a partir de información de la base de datos y la envía al listener.
  private void onRawComidasReceived(PriorityQueue<QueryDocumentSnapshot> rawComidas) {
    ArrayList<Comida> comidas = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawComidas) {
      Comida comida = new Comida(populateRegistro(document));
      Map<String, Object> comidaDocument = document.getData();
      Long proteinas = (Long) comidaDocument.get(COMIDA_PROTEINAS);
      comida.setProteinas((proteinas == null? -1 : proteinas.intValue()));
      Long carbohidratos = (Long) comidaDocument.get(COMIDA_CARBOHIDRATOS);
      comida.setCarbohidratos((carbohidratos == null? -1 : carbohidratos.intValue()));
      Long grasas = (Long) comidaDocument.get(COMIDA_GRASAS);
      comida.setGrasas((grasas == null? -1 : grasas.intValue()));
      comida.setFotoUrl((String) comidaDocument.get(COMIDA_FOTO));
      comidas.add(comida);
    }
    dbResponseListener.onComidasReceived(comidas);
  }

  // Pide carga de registros de bebida a la base de datos.
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadBebidas(Profile profile, Date date) {
    loadRegistros(profile.getId(), BEBIDA_TIPO, date);
  }

  // Crea un objeto de bebida a partir de información de la base de datos y la envía al listener.
  private void onRawBebidasReceived(PriorityQueue<QueryDocumentSnapshot> rawBebidas) {
    ArrayList<Bebida> bebidas = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawBebidas) {
      Bebida bebida = new Bebida(populateRegistro(document));
      Map<String, Object> bebidaDocument = document.getData();
      Long sodio = (Long) bebidaDocument.get(BEBIDA_SODIO);
      bebida.setmSodio((sodio == null? -1 : sodio.intValue()));
      Long azucares = (Long) bebidaDocument.get(BEBIDA_AZUCARES);
      bebida.setmAzucares((azucares == null? -1 : azucares.intValue()));
      Long cantidad = (Long) bebidaDocument.get(BEBIDA_CANTIDAD);
      bebida.setmCantidad((cantidad == null? -1 : cantidad.intValue()));
      bebidas.add(bebida);
    }
    dbResponseListener.onBebidasReceived(bebidas);
  }

  // Pide carga de registros de ejercicio a la base de datos.
  @RequiresApi(api = Build.VERSION_CODES.N)
  public void loadEjercicios(Profile profile, Date date) {
    loadRegistros(profile.getId(), EJERCICIO_TIPO, date);
  }

  // Crea un objeto de ejercicio a partir de información de la base de datos y la envía al listener.
  private void onRawEjercicioReceived(PriorityQueue<QueryDocumentSnapshot> rawEjercicio) {
    ArrayList<Ejercicio> ejercicios = new ArrayList<>();
    for (QueryDocumentSnapshot document: rawEjercicio) {
      Ejercicio ejercicio = new Ejercicio(populateRegistro(document));
      Map<String, Object> ejercicioDocument = document.getData();
      Long intensidad = (Long) ejercicioDocument.get(EJERCICIO_INTENSIDAD);
      ejercicio.setIntensidad((intensidad == null? 1 : intensidad.intValue()));
      ejercicio.setDuracion((String) ejercicioDocument.get(EJERCICIO_DURACION));
      ejercicios.add(ejercicio);
    }
    dbResponseListener.onEjerciciosReceived(ejercicios);
  }

  // Registra la asociación de un usuario con un doctor y la envía el listener.
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
            db.collection(COLLECTION_DOCTORES).document(doctorId).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                  if (task.isSuccessful()) {
                    String nombreDoctor = (String) task.getResult().get(DOCTOR_NOMBRE);
                    profile.setNombreDoctor(nombreDoctor);
                    Map<String, Object> formattedProfile = new HashMap<>();
                    formattedProfile.put(DOCTOR_NOMBRE, nombreDoctor);
                    formattedProfile.put(PROFILE_DOCTOR_REF, task.getResult().getReference());
                    db.collection(COLLECTION_PROFILE).document(profile.getId()).set(
                        formattedProfile, SetOptions.merge());
                    dbResponseListener.onNewDoctorAssociated(profile);
                  } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                    dbResponseListener.onDatabaseNetworkError();
                  }
                }
              });
          } else {
            Log.d(TAG, "Error getting documents: ", task.getException());
            dbResponseListener.onDatabaseNetworkError();
          }
        }
      });
  }



}
