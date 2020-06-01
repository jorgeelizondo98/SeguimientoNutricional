package com.example.seguimientonutricional.ui.account;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.seguimientonutricional.R;


// Fragmento para el inicio de sesión con correo electrónico.
public class EmailLoginFragment extends Fragment {

  OnFragmentInteractionListener mListener;

  private EditText email;
  private EditText password;

  // Constructor vacío obligatorio requerido.
  public EmailLoginFragment() {}

  // Método estático para la generación de instancias del fragment.
  public static EmailLoginFragment newInstance() {
    Bundle args = new Bundle();

    EmailLoginFragment fragment = new EmailLoginFragment();
    fragment.setArguments(args);
    return fragment;
  }

  // Interfaz para comunicar a la actividad llamadora que se han ingresado los datos.
  interface OnFragmentInteractionListener {
    void onEmailLogin(String email, String password);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View rootView = inflater.inflate(R.layout.fragment_email_login, container,
        false);

    email = rootView.findViewById(R.id.email);
    password = rootView.findViewById(R.id.password);

    // Al ingresar los datos para el inicio de sesión, enviarle los datos al listener.
    rootView.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email_str = email.getText().toString();
        final String password_str = password.getText().toString();

        mListener.onEmailLogin(email_str, password_str);
      }
    });

    return rootView;
  }

  // Forzar a las clases que utilicen este fragmento a implementar la interface de listener.
  @Override
  public void onAttach(Context context){
    super.onAttach(context);

    if(context instanceof OnFragmentInteractionListener){
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new ClassCastException(context.toString() +
          "Should implement OnFragmentInteractionListener");
    }
  }

}
