package com.example.seguimientonutricional;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.seguimientonutricional.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ComidaFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener,
        DBController.DBResponseListener {

    //UI
    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup carbohidratosRadioGroup;
    private RadioGroup proteinasRadioGroup;
    private RadioGroup grasasRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private EditText comentarioDoctor;
    private ImageView imagen;
    private ImageView addPhotoButton;
    private Bitmap imageBitmap;

    //DB Controller
    private DBController db;
    private Profile profile;
    FragmentManager fm;
    private HomeViewModel homeViewModel;

    private Integer carbohidratos;
    private Integer proteinas;
    private Integer grasas;
    private Date fecha;
    private Integer hour;
    private Integer minutes;
    private Comida currentComida;
    private Boolean newComida;
    private Boolean formComplete;
    private String id;
    private Calendar c;
    private String currentPhotoPath;

    //ImageCapture request code
    private static final int IMAGE_CAPTURE = 101;


    public ComidaFormsFragment() {
        newComida = true;
    }

    //Constructor for updating the object
    public ComidaFormsFragment(Comida comida) {
        currentComida = comida;
        newComida = false;
    }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
  }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_comida_forms, container, false);


        //Initializing dbController
        fm = getActivity().getSupportFragmentManager();
        final Fragment fragment = getParentFragmentManager().findFragmentByTag("comidaForm");
        db = new DBController(fragment);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        //Connecting to UI
        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);
        imagen = root.findViewById(R.id.imagen_view_id);
        carbohidratosRadioGroup = root.findViewById(R.id.carbohidratos_radiogroup_id);
        proteinasRadioGroup = root.findViewById(R.id.proteinas_radiogroup_id);
        grasasRadioGroup = root.findViewById(R.id.grasas_radiogroup_id);
        comentarioDoctor = root.findViewById(R.id.comentario_Doctor_id);
        addPhotoButton = root.findViewById(R.id.addPhoto_id);


        //Disable editing in Doctors field
        comentarioDoctor.setKeyListener(null);

        fecha = Calendar.getInstance().getTime();
        formComplete = true;
        imageBitmap = null;

        //If updating object we populate the form
        if(currentComida != null){
            setUI();
        }

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("es");
                Locale.setDefault(locale);
                Configuration config =
                        getActivity().getBaseContext().getResources().getConfiguration();
                config.setLocale(locale);
                getActivity().createConfigurationContext(config);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(ComidaFormsFragment.this,1);
                timePicker.show(getParentFragment().getChildFragmentManager(), "time picker");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formComplete = true;
                checksAllFormFilled();
                if(formComplete) {
                    addComida();
                    FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                    getParentFragment().getChildFragmentManager().popBackStackImmediate();
                    fragmentHome.onFormsClosed();
                }else{
                    Toast.makeText(getActivity()
                            ,"Favor de llenar todos los campos"
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                getParentFragment().getChildFragmentManager().popBackStackImmediate();
                fragmentHome.onFormsClosed();
            }
        });


        carbohidratosRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = carbohidratosRadioGroup.findViewById(checkedId);
                int selection = carbohidratosRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: carbohidratos = 0; break;
                    case 2: carbohidratos = 1; break;
                    case 3: carbohidratos = 2; break;
                    case 4: carbohidratos = 3; break;
                    case 5: carbohidratos = 4; break;
                    case 6: carbohidratos = 5; break;
                }
            }
        });

        proteinasRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = proteinasRadioGroup.findViewById(checkedId);
                int selection = proteinasRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: proteinas = 0; break;
                    case 2: proteinas = 1; break;
                    case 3: proteinas = 2; break;
                    case 4: proteinas = 3; break;
                    case 5: proteinas = 4; break;
                    case 6: proteinas = 5; break;
                }
            }
        });

        grasasRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = grasasRadioGroup.findViewById(checkedId);
                int selection = grasasRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: grasas = 0; break;
                    case 2: grasas = 1; break;
                    case 3: grasas = 2; break;
                    case 4: grasas = 3; break;
                    case 5: grasas = 4; break;
                    case 6: grasas = 5; break;
                }
            }
        });

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });
        return root;
    }


    private void addComida() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        fecha = cal.getTime();
        Comida comida = new Comida();
        comida.setTitulo(titulo.getText().toString());
        comida.setDescripcion(descripcion.getText().toString());
        comida.setFecha(fecha);
        comida.setCarbohidratos(carbohidratos);
        comida.setGrasas(grasas);
        comida.setProteinas(proteinas);

        //Adds or update object
        if(newComida){
            id = db.addComida(profile, comida);
            db.addComida(profile, comida);
        }
        //If there's an image we upload it
        if(imageBitmap !=  null){
            if(comida.getId() == null){
                comida.setId(id);
            }
            db.uploadPhotoAndUpdateComida(profile,comida,imageBitmap);
        }
    }

    //Modifies global variable to false if something is missing to be filled by the user.
    private void checksAllFormFilled(){
        if(titulo.getText() == null){formComplete = false;}
        if(descripcion.getText() == null){formComplete = false;}
        if(carbohidratos == null){formComplete = false;}
        if(proteinas == null){formComplete = false;}
        if(grasas == null){formComplete = false;}
        if (hour == null){formComplete = false;}
    }

    //Adds values when clicked from RecyclerView Grid
    public  void setUI(){
        titulo.setText(currentComida.getTitulo());
        descripcion.setText(currentComida.getDescripcion());

        carbohidratos = currentComida.getCarbohidratos();
        grasas = currentComida.getGrasas();
        proteinas = currentComida.getProteinas();

        carbohidratosRadioGroup.check(carbohidratosRadioGroup.getChildAt(
                carbohidratos + 1).getId());
        proteinasRadioGroup.check(proteinasRadioGroup.getChildAt(
                proteinas + 1 ).getId());
        grasasRadioGroup.check(grasasRadioGroup.getChildAt(
                grasas + 1).getId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentComida.getFecha());
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        buttonHora.setText(hour.toString() + ":" + minutes.toString());

        if(currentComida.getComentario() != null){
            comentarioDoctor.setText(currentComida.getComentario());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startCamera(){
        if(hasCameraPermission()){
            //display camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, IMAGE_CAPTURE);
        } else {
            requestCameraPermission();
        }
    }


    //checks if the user already gave permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasCameraPermission() {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else
            return false;
    }

    //asks for camera and taking picture permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestCameraPermission() {
        String[] permissionRequest = {Manifest.permission.CAMERA};
        requestPermissions(permissionRequest,IMAGE_CAPTURE);
        startCamera();
    }

    //Gets data from image taken by the user.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE) {
            if (resultCode == getActivity().RESULT_OK) {
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                imagen.setImageBitmap(imageBitmap);
            }
            else if (resultCode == getActivity().RESULT_CANCELED) {

            } else {

            }
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Removes calendar button from title bar
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }

    //TimePicker Listener
    @Override
    public void onTimeSet(int hour, int minute) {
        this.hour =  hour;
        this.minutes = minute;
        buttonHora.setText(Integer.toString(hour) + ":"+ Integer.toString(minute));
    }


    //DBCONTROLLER LISTENERS
    @Override
    public void onDatabaseNetworkError() {

    }

    @Override
    public void onProfileReceived(Profile profile) {
        this.profile = profile;
        Log.d("checa", this.profile.getName());
    }

    @Override
    public void onComidasReceived(ArrayList<Comida> comidas) {

    }

    @Override
    public void onBebidasReceived(ArrayList<Bebida> bebidas) {

    }

    @Override
    public void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios) {

    }

    @Override
    public void onNewDoctorAssociated(Profile profile) {

    }

    //Receives Date selected on Calendar from HomeViewModel
    @Override
    public void onComidaPhotoAdded(Comida comida) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ViewModelProviders.of(getActivity()).get(HomeViewModel.class).getDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable Date newDate) {
                fecha = newDate;
            }
        });
    }




}
