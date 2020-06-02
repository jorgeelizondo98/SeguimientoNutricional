package com.example.seguimientonutricional;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class EjercicioFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener,
        DBController.DBResponseListener {

    //UI
    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup intensidadRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private EditText horasEditText;
    private EditText minutosEditText;
    private EditText comentarioDoctor;
    private Calendar c;

    //DBController
    private DBController db;
    private Profile profile;
    FragmentManager fm;

    private HomeViewModel homeViewModel;
    private Integer intensidad;
    private Integer calidad;
    private Date fecha;
    private Integer hour;
    private Integer minutes;
    private Ejercicio currentEjercicio;
    private Boolean newEjercicio;
    boolean formComplete;

    public EjercicioFormsFragment() {
        // Required empty public constructor
        newEjercicio = true;
    }

    //Constructor for updating the object
    public EjercicioFormsFragment(Ejercicio ejercicio) {
        currentEjercicio = ejercicio;
        newEjercicio = false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_ejercicio_forms, container, false);

        //Initializing DBController
        fm = getActivity().getSupportFragmentManager();
        Fragment fragment = getParentFragmentManager().findFragmentByTag("ejercicioForm");
        db = new DBController(fragment);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        //Connecting to layout
        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);
        intensidadRadioGroup = root.findViewById(R.id.intensidad_radiogroup_id);
        horasEditText = root.findViewById(R.id.horas_id);
        minutosEditText = root.findViewById(R.id.minutos_id);
        comentarioDoctor = root.findViewById(R.id.comentario_Doctor_id);

        //Disable editing in Doctors field
        comentarioDoctor.setKeyListener(null);

        fecha = Calendar.getInstance().getTime();

        //If updating object we populate the form
        if(currentEjercicio != null){
            setUI();
        }

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(EjercicioFormsFragment.this,1);
                timePicker.show(getParentFragment().getChildFragmentManager(), "time picker");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check if everything is filled up
                formComplete = true;
                checksAllFormFilled();
                if(formComplete) {
                    addEjercicio();
                    //Go back to parent Fragment
                    FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                    getParentFragment().getChildFragmentManager().popBackStackImmediate();
                    fragmentHome.onFormsClosed();
                } else {
                    Toast.makeText(getActivity()
                            ,"Favor de llenar todos los campos"
                            ,Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to parent Fragment
                FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                getParentFragment().getChildFragmentManager().popBackStackImmediate();
                fragmentHome.onFormsClosed();
            }
        });



        intensidadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = intensidadRadioGroup.findViewById(checkedId);
                int selection = intensidadRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: intensidad = 0; break;
                    case 2: intensidad = 1; break;
                    case 3: intensidad = 2; break;
                    case 4: intensidad = 3; break;
                    case 5: intensidad = 4; break;
                    case 6: intensidad = 5; break;
                }
            }
        });

        return root;
    }


    //Adds values when clicked from RecyclerView Grid
    public  void setUI(){
        titulo.setText(currentEjercicio.getTitulo());
        descripcion.setText(currentEjercicio.getDescripcion());
        intensidad = currentEjercicio.getIntensidad();
        minutosEditText.setText(currentEjercicio.getMinutes());
        horasEditText.setText(currentEjercicio.getHour());
        intensidadRadioGroup.check(intensidadRadioGroup.getChildAt(
                intensidad + 1 ).getId());
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentEjercicio.getFecha());
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        buttonHora.setText(hour.toString() + ":" + minutes.toString());

        if(currentEjercicio.getComentario() != null){
            comentarioDoctor.setText(currentEjercicio.getComentario());
        }
    }



    private void addEjercicio() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        cal.setTimeZone(TimeZone.getDefault());
        fecha = cal.getTime();
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo(titulo.getText().toString());
        ejercicio.setDescripcion(descripcion.getText().toString());
        ejercicio.setFecha(fecha);
        ejercicio.setDuracion(horasEditText.getText() + " Horas"
                + minutosEditText.getText() + " minutos" );
        ejercicio.setIntensidad(intensidad);

        //Add or update object
        if(newEjercicio){
            db.addEjercicio(profile, ejercicio);
        } else {
            ejercicio.setId(currentEjercicio.getId());
            db.updateEjercicio(profile,ejercicio);
        }
    }


    //Modifies global variable to false if something is missing to be filled by the user.
    private void checksAllFormFilled(){
        if(titulo.getText() == null){formComplete = false;}
        if(descripcion.getText() == null){formComplete = false;}
        if(horasEditText.getText() == null){formComplete = false;}
        if(minutosEditText.getText() == null){formComplete = false;}
        if(intensidad == null){formComplete = false;}
        if (hour == null){formComplete = false;}
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        //Removes calendar button from titlebar
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }

    //DB CONTROLLER LISTENERS
    @Override
    public void onDatabaseNetworkError() {

    }

    @Override
    public void onProfileReceived(Profile profile) throws ParseException {
        this.profile = profile;
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

    //Receives date selected by calendar with HomeViewModel
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

    //TIME PICKER LISTENER
    @Override
    public void onTimeSet(int hour, int minute) {
        this.hour =  hour;
        this.minutes = minute;
        buttonHora.setText(Integer.toString(hour) + ":"+ Integer.toString(minute));
    }
}
