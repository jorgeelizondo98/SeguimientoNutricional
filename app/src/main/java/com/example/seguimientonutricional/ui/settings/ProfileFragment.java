package com.example.seguimientonutricional.ui.settings;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.Profile;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.ui.home.tabsFragments.ComidasFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class ProfileFragment extends Fragment implements DBController.DBResponseListener{


    private Button buttonGuardar;
    private ImageView imagenUsuario;
    private TextView mailUsuario;
    private EditText nombreUsuario;
    private EditText apellidoPaterno;
    private EditText apellidoMaterno;
    private String name;
    private String apellidoP;
    private String apellidoM;

    private DBController db;
    private Profile profile;
    FragmentManager fm;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        List<Fragment> allFragments = getParentFragment().getChildFragmentManager().getFragments();
        //Fragment fragment = getParentFragment().getChildFragmentManager().findFragmentById(R.id.profile_fragment);

        //We get the actual fragment running
        for (Fragment fragmento: allFragments) {
            if (fragmento instanceof ProfileFragment){
                db = new DBController(fragmento);
            }
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);


        buttonGuardar = root.findViewById(R.id.button_save);
        mailUsuario = root.findViewById(R.id.text_email);
        imagenUsuario = root.findViewById(R.id.imageProfile_view);
        nombreUsuario = root.findViewById(R.id.profileName);
        apellidoPaterno = root.findViewById(R.id.profileLastName);
        apellidoMaterno = root.findViewById(R.id.profileSecondLastName);

        nombreUsuario.setCursorVisible(false);
        apellidoMaterno.setCursorVisible(false);
        apellidoPaterno.setCursorVisible(false);

        if(profile != null){
            profile.setEmail(currentUser.getEmail());
        }

        mailUsuario.setText(currentUser.getEmail());


        nombreUsuario.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        apellidoPaterno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });

        apellidoMaterno.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    hideKeyboard(v);
                }
            }
        });


        //nombreUsuario.setText("Test");
        //nombreUsuario.setHint(profile.getName());

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    name = nombreUsuario.getText().toString();
                    apellidoP = apellidoPaterno.getText().toString();
                    apellidoM = apellidoMaterno.getText().toString();
                    profile.setName(name,apellidoP,apellidoM);
                    if(DataExists()) {
                        db.updateProfile(profile);
                        Toast.makeText(getActivity(), "Datos Guardados", Toast.LENGTH_LONG).show();
                    } else{
                        Toast.makeText(getActivity(), "Faltan Datos", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Toast.makeText(getActivity(), "Error al Guardar", Toast.LENGTH_LONG).show();
                }

            }
        });


        return root;

    }

    @Override
    public void onDatabaseNetworkError() {

    }

    public boolean DataExists(){
        if(name == null){ return false;}
        if(apellidoM == null){return false;}
        if(apellidoP == null){return false;}
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    @Override
    public void onComidaPhotoAdded(Comida comida) {

    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }






}
