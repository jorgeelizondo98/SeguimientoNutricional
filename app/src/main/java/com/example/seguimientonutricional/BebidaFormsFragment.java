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

    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup cantidadRadioGroup;
    private RadioGroup calidadRadioGroup;
    private RadioGroup carbohidratosRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private Calendar c;

    private DBController db;
    private Profile profile;
    FragmentManager fm;
    private HomeViewModel homeViewModel;


    private Boolean carbohidratos;
    private Integer cantidad;
    private Integer calidad;
    private Date fecha;
    private Integer hour;
    private Integer minutes;
    private Bebida currentBebida;
    private Boolean newBebida;

    public BebidaFormsFragment() {
        // Required empty public constructor
        newBebida = true;
    }

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

        fm = getActivity().getSupportFragmentManager();
        Fragment fragment = getParentFragmentManager().findFragmentByTag("bebidaForm");
        db = new DBController(fragment);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);

        fecha = Calendar.getInstance().getTime();

        if(currentBebida != null){
            titulo.setText(currentBebida.getTitulo());
            descripcion.setText(currentBebida.getDescripcion());
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
                addBebida();
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

        carbohidratosRadioGroup = root.findViewById(R.id.carbohidratos_radiogroup_id);
        cantidadRadioGroup = root.findViewById(R.id.proteinas_radiogroup_id);
        calidadRadioGroup = root.findViewById(R.id.grasas_radiogroup_id);

        carbohidratosRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = carbohidratosRadioGroup.findViewById(checkedId);
                int selection = carbohidratosRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: carbohidratos = true; break;
                    case 2: carbohidratos = false; break;
                }
            }
        });

        cantidadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = cantidadRadioGroup.findViewById(checkedId);
                int selection = cantidadRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: cantidad = 1; break;
                    case 2: cantidad = 2; break;
                    case 3: cantidad = 3; break;
                    case 4: cantidad = 4; break;
                    case 5: cantidad = 5; break;
                }
            }
        });

        calidadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = calidadRadioGroup.findViewById(checkedId);
                int selection = calidadRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: calidad = 1; break;
                    case 2: calidad = 2; break;
                    case 3: calidad = 3; break;
                    case 4: calidad = 4; break;
                    case 5: calidad = 5; break;
                }
            }
        });

        return root;
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
        bebida.setFecha(fecha);
        if(newBebida){
            db.addBebida(profile, bebida);
        } else {
            db.updateBebida(profile,bebida);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        this.hour =  hour;
        this.minutes = minute;
        buttonHora.setText(Integer.toString(hour) + ":"+ Integer.toString(minute));
    }

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
