package com.example.seguimientonutricional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "LOGIN";

  private static GoogleSignInClient mGoogleSignInClient;
  private static final int GOOGLE_SIGN_IN = 0;

  private EmailLoginFragment emailLoginFragment;
  private EmailRegisterFragment emailRegisterFragment;
  private Fragment current_fragment;
  private Button registerOrLoginButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build();

    // Build a GoogleSignInClient with the options specified by gso.
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    findViewById(R.id.google_sign_in_button).setOnClickListener(clickSignIn);

    registerOrLoginButton = findViewById(R.id.register_login);
    emailLoginFragment = EmailLoginFragment.newInstance();
    emailRegisterFragment = EmailRegisterFragment.newInstance();
    current_fragment = emailRegisterFragment;
    switchFragment(null);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check for existing Google Sign In account, if the user is already signed in
    // the GoogleSignInAccount will be non-null.
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    if (account != null) {
      updateUI(account);
    }
  }

  // Para mostrar el Fragmento
  public void switchFragment(View view){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    if (current_fragment instanceof EmailLoginFragment) {
      fragmentTransaction.add(R.id.account_fragment_container, emailRegisterFragment)
          .addToBackStack(null);
      fragmentTransaction.remove(emailLoginFragment).commit();
      // TODO: Get string from strings file.
      registerOrLoginButton.setText("O iniciar sesión");
      current_fragment = emailRegisterFragment;
    } else {
      fragmentTransaction.add(R.id.account_fragment_container, emailLoginFragment)
          .addToBackStack(null);
      fragmentTransaction.remove(emailRegisterFragment).commit();
      // TODO: Get string from strings file.
      registerOrLoginButton.setText("O registrarse");
      current_fragment = emailLoginFragment;
    }
  }

  private void signIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    if (requestCode == GOOGLE_SIGN_IN) {
      // The Task returned from this call is always completed, no need to attach
      // a listener.
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);

      // Signed in successfully, show authenticated UI.
      updateUI(account);
    } catch (ApiException e) {
      // The ApiException status code indicates the detailed failure reason.
      // Please refer to the GoogleSignInStatusCodes class reference for more information.
      Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
      updateUI(null);
    }
  }

  private void updateUI(GoogleSignInAccount account) {
    if (account == null) {
      Toast.makeText(this, "NULL!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, "INICIADA SESION!", Toast.LENGTH_SHORT).show();
    }
  }

  private View.OnClickListener clickSignIn = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.google_sign_in_button:
          signIn();
          break;
      }
    }
  };
}
