package com.example.seguimientonutricional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class ComidaFormsFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener {

    private Button buttonHora;

    private RadioGroup carbohidratosRadioGroup;
    private RadioGroup proteinasRadioGroup;
    private RadioGroup grasasRadioGroup;


    private Integer carbohidratos;
    private Integer proteinas;
    private Integer grasas;

    public ComidaFormsFragment() {
        // Required empty public constructor
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


        buttonHora = root.findViewById(R.id.button_hora_id);

        buttonHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(ComidaFormsFragment.this,1);
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
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


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(false);
    }

    @Override
    public void onTimeSet(int hour, int minute) {
        buttonHora.setText(Integer.toString(hour));
    }
}
