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
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ComidaFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener,
        DBController.DBResponseListener {

    private Button buttonHora;
    private EditText titulo;
    private EditText descripcion;
    private RadioGroup carbohidratosRadioGroup;
    private RadioGroup proteinasRadioGroup;
    private RadioGroup grasasRadioGroup;
    private Button confirmButton;
    private Button cancelButton;
    private Calendar c;
    private ImageView imagen;

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

    public ComidaFormsFragment() {
    // Required empty public constructor
    }

    public ComidaFormsFragment(Comida comida) {
        // Required empty public constructor
        currentComida = comida;
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
        View root = inflater.inflate(R.layout.fragment_comida_forms, container, false);

        fm = getActivity().getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag("comidaForm");
        db = new DBController(fragment);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

        buttonHora = root.findViewById(R.id.button_hora_id);
        descripcion = root.findViewById(R.id.descripcion_id);
        titulo = root.findViewById(R.id.titulo_id);
        confirmButton = root.findViewById(R.id.okay_button_id);
        cancelButton = root.findViewById(R.id.cancel_button);
        imagen = root.findViewById(R.id.imagen_view_id);

        fecha = Calendar.getInstance().getTime();

        if(currentComida != null){
            titulo.setText(currentComida.getTitulo());
            descripcion.setText(currentComida.getDescripcion());
        }

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(ComidaFormsFragment.this,1);
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComida();
                Fragment fragmentTabs = new ActividadesFragmentTabs();
                fm.beginTransaction().replace(R.id.container_home_content,fragmentTabs).commit();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentTabs = new ActividadesFragmentTabs();
                fm.beginTransaction().replace(R.id.container_home_content,fragmentTabs).commit();
            }
        });


        carbohidratosRadioGroup = root.findViewById(R.id.carbohidratos_radiogroup_id);
        proteinasRadioGroup = root.findViewById(R.id.proteinas_radiogroup_id);
        grasasRadioGroup = root.findViewById(R.id.grasas_radiogroup_id);

        carbohidratosRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = carbohidratosRadioGroup.findViewById(checkedId);
                int selection = carbohidratosRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: carbohidratos = 1; break;
                    case 2: carbohidratos = 2; break;
                    case 3: carbohidratos = 3; break;
                    case 4: carbohidratos = 4; break;
                    case 5: carbohidratos = 5; break;
                }
            }
        });

        proteinasRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = proteinasRadioGroup.findViewById(checkedId);
                int selection = proteinasRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: proteinas = 1; break;
                    case 2: proteinas = 2; break;
                    case 3: proteinas = 3; break;
                    case 4: proteinas = 4; break;
                    case 5: proteinas = 5; break;
                }
            }
        });

        grasasRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = grasasRadioGroup.findViewById(checkedId);
                int selection = grasasRadioGroup.indexOfChild(radioButton);
                switch (selection){
                    case 1: grasas = 1; break;
                    case 2: grasas = 2; break;
                    case 3: grasas = 3; break;
                    case 4: grasas = 4; break;
                    case 5: grasas = 5; break;
                }
            }
        });

        return root;
    }


    private void addComida() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minutes);
        fecha = cal.getTime();
        Comida comida1 = new Comida();
        comida1.setTitulo(titulo.getText().toString());
        comida1.setDescripcion(descripcion.getText().toString());
        comida1.setFecha(fecha);
        db.addComida(profile, comida1);

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
