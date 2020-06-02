package com.example.seguimientonutricional;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class BebidaFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener,
        DBController.DBResponseListener {

    //UI
    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup sodioRadioGroup;
    private RadioGroup azucaresRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private EditText cantidad_et;
    private EditText comentarioDoctor;

    //DBController
    private DBController db;
    private Profile profile;
    FragmentManager fm;
    private HomeViewModel homeViewModel;


    private Integer sodio;
    private Integer azucares;
    private Date fecha;
    private Integer hour;
    private Integer minutes;
    private Bebida currentBebida;
    private Boolean newBebida;
    private Boolean formComplete;
    private Calendar c;

    public BebidaFormsFragment() {
        // Required empty public constructor
        newBebida = true;
    }

    //Constructor for updating the object
    public BebidaFormsFragment(Bebida bebida) {
        currentBebida = bebida;
        newBebida = false;
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
        View root = inflater.inflate(R.layout.fragment_bebida_forms, container, false);

        //Initializing DBController
        fm = getActivity().getSupportFragmentManager();
        Fragment fragment = getParentFragmentManager().findFragmentByTag("bebidaForm");
        db = new DBController(fragment);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        //Connecting to layout
        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);
        cantidad_et = root.findViewById(R.id.cantidad_id);
        sodioRadioGroup = root.findViewById(R.id.sodio_radiogroup_id);
        azucaresRadioGroup = root.findViewById(R.id.azucares_radiogroup_id);
        comentarioDoctor = root.findViewById(R.id.comentario_Doctor_id);


        //Disable editing in Doctors field
        comentarioDoctor.setKeyListener(null);

        formComplete = true;
        fecha = Calendar.getInstance().getTime();

        //If updating object we populate the form
        if(currentBebida != null){
            setUI();
        }

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(BebidaFormsFragment.this,1);
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
                    addBebida();
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
                FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                getParentFragment().getChildFragmentManager().popBackStackImmediate();
                fragmentHome.onFormsClosed();
            }
        });




        sodioRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = sodioRadioGroup.findViewById(checkedId);
                int selection = sodioRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: sodio = 0; break;
                    case 2: sodio = 1; break;
                    case 3: sodio = 2; break;
                    case 4: sodio = 3; break;
                    case 5: sodio = 4; break;
                    case 6: sodio = 5; break;
                }
            }
        });

        azucaresRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = azucaresRadioGroup.findViewById(checkedId);
                int selection = azucaresRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: azucares = 0; break;
                    case 2: azucares = 1; break;
                    case 3: azucares = 2; break;
                    case 4: azucares = 3; break;
                    case 5: azucares = 4; break;
                    case 6: azucares = 5; break;
                }
            }
        });

        return root;
    }


    //Adds values when clicked from RecyclerView Grid
    public  void setUI(){
        titulo.setText(currentBebida.getTitulo());
        descripcion.setText(currentBebida.getDescripcion());
        cantidad_et.setText(currentBebida.getCantidad().toString());
        sodio = currentBebida.getSodio();
        azucares = currentBebida.getAzucares();
        sodioRadioGroup.check(sodioRadioGroup.getChildAt(
                sodio + 1).getId());

        azucaresRadioGroup.check(azucaresRadioGroup.getChildAt(
                azucares + 1 ).getId());

        Calendar cal = Calendar.getInstance();
        cal.setTime(currentBebida.getFecha());
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minutes = cal.get(Calendar.MINUTE);
        buttonHora.setText(hour.toString() + ":" + minutes.toString());

        if(currentBebida.getComentario() != null){
            comentarioDoctor.setText(currentBebida.getComentario());
        }
    }

    private void addBebida() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        fecha = cal.getTime();
        Bebida bebida = new Bebida();
        bebida.setTitulo(titulo.getText().toString());
        bebida.setDescripcion(descripcion.getText().toString());
        bebida.setmAzucares(azucares);
        bebida.setmSodio(sodio);
        bebida.setmCantidad(Integer.parseInt(cantidad_et.getText().toString()));
        bebida.setFecha(fecha);

        //Add or update object
        if(newBebida){
            db.addBebida(profile, bebida);
        } else {
            bebida.setId(currentBebida.getId());
            db.updateBebida(profile,bebida);
        }

    }

    //Modifies global variable to false if something is missing to be filled by the user.
    private void checksAllFormFilled(){
        if(titulo.getText() == null){formComplete = false;}
        if(descripcion.getText() == null){formComplete = false;}
        if(cantidad_et.getText() == null){formComplete = false;}
        if(sodio == null){formComplete = false;}
        if(azucares == null){formComplete = false;}
        if (hour == null){formComplete = false;}
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        //Removes calendar button from titlebar
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }

    //TIME PICKER LISTENER
    @Override
    public void onTimeSet(int hour, int minute) {
        this.hour =  hour;
        this.minutes = minute;
        buttonHora.setText(Integer.toString(hour) + ":"+ Integer.toString(minute));
    }

    //DB CONTROLLER LISTENERS
    @Override
    public void onDatabaseNetworkError() {

    }

    @Override
    public void onProfileReceived(Profile profile) throws ParseException {
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

    //Receives date selected by calendar with HomeViewModel
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
