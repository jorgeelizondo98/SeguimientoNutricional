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
public class EmailRegisterFragment extends Fragment {

  OnFragmentInteractionListener mListener;

  private EditText email;
  private EditText password;
  private EditText confirm_password;

  public EmailRegisterFragment() {}

  public static EmailRegisterFragment newInstance() {
    Bundle args = new Bundle();

    EmailRegisterFragment fragment = new EmailRegisterFragment();
    fragment.setArguments(args);
    return fragment;
  }

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
