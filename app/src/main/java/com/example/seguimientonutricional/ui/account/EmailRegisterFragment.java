package com.example.seguimientonutricional.ui.account;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.seguimientonutricional.R;


// Fragmento para el registro con correo electrónico.
public class EmailRegisterFragment extends Fragment {

  OnFragmentInteractionListener mListener;

  private EditText email;
  private EditText password;
  private EditText confirm_password;
  private CardView cardView;

  // Constructor vacío obligatorio requerido.
  public EmailRegisterFragment() {}

  // Método estático para la generación de instancias del fragment.
  public static EmailRegisterFragment newInstance() {
    Bundle args = new Bundle();

    EmailRegisterFragment fragment = new EmailRegisterFragment();
    fragment.setArguments(args);
    return fragment;
  }

  // Interfaz para comunicar a la actividad llamadora que se han ingresado los datos.
  interface OnFragmentInteractionListener {
    void onEmailRegister(String email, String password, String confirm_password);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View rootView = inflater.inflate(R.layout.fragment_email_register, container,
        false);

    email = rootView.findViewById(R.id.email);
    password = rootView.findViewById(R.id.password);
    confirm_password = rootView.findViewById(R.id.confirm_password);
    cardView = getActivity().findViewById(R.id.card);

    // Al ingresar los datos para el inicio de sesión, enviarle los datos al listener.
    rootView.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email_str = email.getText().toString();
        final String password_str = password.getText().toString();
        final String confirm_password_str = confirm_password.getText().toString();
        mListener.onEmailRegister(email_str, password_str, confirm_password_str);
      }
    });

    return rootView;
  }

  @Override
  public void onResume() {
    super.onResume();
    cardView.setVisibility(View.VISIBLE);
  }

  // Forzar a las clases que utilicen este fragmento a implementar la interface de listener.
  @Override
  public void onAttach(Context context){
    super.onAttach(context);

    if(context instanceof  OnFragmentInteractionListener){
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new ClassCastException(context.toString() +
          "Should implement OnFragmentInteractionListener");
    }
  }

}
