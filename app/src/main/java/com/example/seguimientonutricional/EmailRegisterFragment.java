package com.example.seguimientonutricional;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailRegisterFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  OnFragmentInteractionListener mListener;

  private EditText email;
  private EditText password;
  private EditText confirm_password;

  public EmailRegisterFragment() {

  }

  public static EmailRegisterFragment newInstance() {
    Bundle args = new Bundle();

    EmailRegisterFragment fragment = new EmailRegisterFragment();
    fragment.setArguments(args);
    return fragment;
  }

  interface OnFragmentInteractionListener {
    void onRegister(String email, String password, String confirm_password);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
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

    rootView.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email_str = email.getText().toString();
        final String password_str = password.getText().toString();
        final String confirm_password_str = confirm_password.getText().toString();

        mListener.onRegister(email_str, password_str, confirm_password_str);
      }
    });

    return rootView;
  }

  @Override
  public void onAttach(Context context){
    super.onAttach(context);

    if(context instanceof  OnFragmentInteractionListener){
      mListener =(OnFragmentInteractionListener) context;
    } else {
      throw new ClassCastException(context.toString() +
          "Should implement OnFragmentInteractionListener");
    }
  }
}
