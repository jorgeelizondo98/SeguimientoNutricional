package com.example.seguimientonutricional.ui.account;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.seguimientonutricional.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailLoginFragment extends Fragment {

  OnFragmentInteractionListener mListener;

  private EditText email;
  private EditText password;

  public EmailLoginFragment() {

  }

  public static EmailLoginFragment newInstance() {
    Bundle args = new Bundle();

    EmailLoginFragment fragment = new EmailLoginFragment();
    fragment.setArguments(args);
    return fragment;
  }

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

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (savedInstanceState != null) {
      email.setText(savedInstanceState.getString("email"));
      password.setText(savedInstanceState.getString("password"));
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putString("email", email.getText().toString());
    outState.putString("password", password.getText().toString());
  }
}
