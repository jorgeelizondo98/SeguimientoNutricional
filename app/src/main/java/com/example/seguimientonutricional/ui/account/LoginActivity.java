package com.example.seguimientonutricional.ui.account;

import androidx.annotation.NonNull;
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

import com.example.seguimientonutricional.MainActivity;
import com.example.seguimientonutricional.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
    implements EmailRegisterFragment.OnFragmentInteractionListener,
        EmailLoginFragment.OnFragmentInteractionListener {

  private static final String TAG = "LOGIN";

  private GoogleSignInClient mGoogleSignInClient;
  private CallbackManager mFacebookCallbackManager;
  private static final int GOOGLE_SIGN_IN = 0;
  private static final int FACEBOOK_SIGN_IN = 64206;
  private static final int LOGOUT = 2;

  private EmailLoginFragment emailLoginFragment;
  private EmailRegisterFragment emailRegisterFragment;
  private Fragment current_fragment;

  private FirebaseAuth mAuth;

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

    // Firebase auth for Email and Facebook Signup.
    mAuth = FirebaseAuth.getInstance();

    // Email signup.
    emailLoginFragment = EmailLoginFragment.newInstance();
    emailRegisterFragment = EmailRegisterFragment.newInstance();
    current_fragment = emailRegisterFragment;
    switchEmailFragment(null);

    // Facebook login.
    mFacebookCallbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
        loginResultFacebookCallback);
  }

  @Override
  protected void onStart() {
    super.onStart();

    // Check for existing Google Sign In account, if the user is already signed in
    // the GoogleSignInAccount will be non-null.
    GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);
    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser firebaseOrFacebookUser = mAuth.getCurrentUser();

    if (googleUser != null) {
      updateUI();
    } else if (firebaseOrFacebookUser != null) {
      updateUI();
    }
  }

  public void switchEmailFragment(View view){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    // TODO: Revisar si lo puedo sacar del parametro view.
    Button registerOrLoginButton = findViewById(R.id.register_login);

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

  @Override
  public void onEmailRegister(String email, String password, String confirm_password) {
    if (password.equals(confirm_password)) {
      mAuth.createUserWithEmailAndPassword(email, password)
          .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI();
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                // TODO: Get string from strings file.
                Toast.makeText(LoginActivity.this, "Registro fallido.",
                    Toast.LENGTH_SHORT).show();
              }
            }
          });
    } else {
      // TODO: Sacar del archivo de strings.
      Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onEmailLogin(String email, String password) {
    mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithEmail:success");
              FirebaseUser user = mAuth.getCurrentUser();
              updateUI();
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithEmail:failure", task.getException());
              // TODO: Get str from strings file.
              Toast.makeText(LoginActivity.this, "Fallo login", Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void googleSignIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
  }

  FacebookCallback<LoginResult> loginResultFacebookCallback = new FacebookCallback<LoginResult>() {
    @Override
    public void onSuccess(LoginResult loginResult) {
      handleFacebookAccessToken(loginResult.getAccessToken());
    }

    @Override
    public void onCancel() {
      // TODO: Get str from strings file.
      Toast.makeText(LoginActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException exception) {
      // TODO: Get str from strings file.
      Toast.makeText(LoginActivity.this, "Fallo login", Toast.LENGTH_SHORT).show();
    }
  };

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    if (requestCode == GOOGLE_SIGN_IN) {
      // The Task returned from this call is always completed, no need to attach
      // a listener.
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleGoogleSignInResult(task);
    } else if (requestCode == FACEBOOK_SIGN_IN) {
      mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    } else if (requestCode == LOGOUT) {
      mAuth.signOut();
      mGoogleSignInClient.signOut();
      LoginManager.getInstance().logOut();
    }
  }

  private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);
      // Signed in successfully, show authenticated UI.
      updateUI();
    } catch (ApiException e) {
      // The ApiException status code indicates the detailed failure reason.
      // Please refer to the GoogleSignInStatusCodes class reference for more information.
      Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
      // TODO: Get str from strings file.
      Toast.makeText(this, "Fallo login", Toast.LENGTH_SHORT).show();
    }
  }

  private void handleFacebookAccessToken(AccessToken token) {
    Log.d(TAG, "handleFacebookAccessToken:" + token);

    AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithCredential:success");
              FirebaseUser user = mAuth.getCurrentUser();
              updateUI();
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithCredential:failure", task.getException());
              // TODO: Get str from strings file.
              Toast.makeText(LoginActivity.this, "Authentication failed.",
                  Toast.LENGTH_SHORT).show();
            }
          }
        });
  }

  private void updateUI() {
    Intent intent = new Intent(this, MainActivity.class);
    startActivityForResult(intent, LOGOUT);
  }

  private View.OnClickListener clickSignIn = new View.OnClickListener() {
    @Override
    public void onClick(View view) {
      switch (view.getId()) {
        case R.id.google_sign_in_button:
          googleSignIn();
          break;
      }
    }
  };
}

