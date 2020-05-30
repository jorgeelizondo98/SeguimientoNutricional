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
public class EjercicioFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener,
        DBController.DBResponseListener {

    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup intensidadRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private Button duracionButton;
    private Calendar c;

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

    public EjercicioFormsFragment() {
        // Required empty public constructor
        newEjercicio = true;
    }

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

        fm = getActivity().getSupportFragmentManager();
        Fragment fragment = getParentFragmentManager().findFragmentByTag("ejercicioForm");
        db = new DBController(fragment);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);
        intensidadRadioGroup = root.findViewById(R.id.intensidad_radiogroup_id);
        duracionButton = root.findViewById(R.id.duarcion_button);


        fecha = Calendar.getInstance().getTime();

        if(currentEjercicio != null){
            titulo.setText(currentEjercicio.getTitulo());
            descripcion.setText(currentEjercicio.getDescripcion());
        }

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(EjercicioFormsFragment.this,1);
                timePicker.show(getParentFragment().getChildFragmentManager(), "time picker");
            }
        });

        duracionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(EjercicioFormsFragment.this,2);
                timePicker.show(getParentFragment().getChildFragmentManager(), "time picker");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEjercicio();
                FormsLifeCyle fragmentHome = (FormsLifeCyle) getParentFragment();
                getParentFragment().getChildFragmentManager().popBackStackImmediate();
                fragmentHome.onFormsClosed();
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



        intensidadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = intensidadRadioGroup.findViewById(checkedId);
                int selection = intensidadRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: intensidad = 1; break;
                    case 2: intensidad = 2; break;
                    case 3: intensidad = 3; break;
                    case 4: intensidad = 4; break;
                    case 5: intensidad = 5; break;
                }
            }
        });

        return root;
    }

    private void addEjercicio() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        fecha = cal.getTime();
        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setTitulo(titulo.getText().toString());
        ejercicio.setDescripcion(descripcion.getText().toString());
        ejercicio.setFecha(fecha);
        if(newEjercicio){
            db.addEjercicio(profile, ejercicio);
        } else {
            db.updateEjercicio(profile,ejercicio);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }


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

    @Override
    public void onTimeSet(int hour, int minute) {
        this.hour =  hour;
        this.minutes = minute;
        buttonHora.setText(Integer.toString(hour) + ":"+ Integer.toString(minute));
    }
}
