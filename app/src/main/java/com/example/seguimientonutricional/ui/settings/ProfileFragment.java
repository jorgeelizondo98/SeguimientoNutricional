package com.example.seguimientonutricional.ui.settings;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Profile;
import com.example.seguimientonutricional.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {

    private Button buttonGuardar;
    private ImageView imagenUsuario;
    private TextView mailUsuario;
    private EditText nombreUsuario;
    private EditText apellidoPaterno;
    private EditText apellidoMaterno;

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

        //db = new DBController(this);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //db.loadProfile(currentUser);


        buttonGuardar = root.findViewById(R.id.button_save);
        mailUsuario = root.findViewById(R.id.text_email);
        imagenUsuario = root.findViewById(R.id.imageProfile_view);
        nombreUsuario = root.findViewById(R.id.profileName);
        apellidoPaterno = root.findViewById(R.id.profileLastName);
        apellidoMaterno = root.findViewById(R.id.profileSecondLastName);

        mailUsuario.setText(currentUser.getEmail());
        //nombreUsuario.setText(currentUser.);




        //nombreUsuario.setText(currentUser.get);





        return root;

    }
}
