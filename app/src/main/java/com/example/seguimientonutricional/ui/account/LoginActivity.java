package com.example.seguimientonutricional.ui.account;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.seguimientonutricional.DBController;
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
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity
    implements EmailRegisterFragment.OnFragmentInteractionListener,
        EmailLoginFragment.OnFragmentInteractionListener, QrFragment.OnQrFragmentInteractionListener {

  private static final String TAG = "LOGIN";

  private GoogleSignInClient mGoogleSignInClient;
  private CallbackManager mFacebookCallbackManager;
  private FirebaseAuth mAuth;

  private static final int GOOGLE_SIGN_IN = 0;
  private static final int FACEBOOK_SIGN_IN = 64206;
  private static final int LOGOUT = 2;

  private FragmentManager fragmentManager;
  private Fragment currentFragment;
  private static final String LOGIN_FRAGMENT = "login";
  private static final String REGISTER_FRAGMENT = "register";

  private CardView cardView;
  private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;
  private FirebaseUser notRegisteredUser;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_login);


    cardView = findViewById(R.id.card);
    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.default_web_client_id))
        .requestEmail()
        .build();
    // Build a GoogleSignInClient with the options specified by gso.
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    findViewById(R.id.google_sign_in_button).setOnClickListener(clickSignIn);

    // Firebase auth for Email and Facebook Signup.
    mAuth = FirebaseAuth.getInstance();

    fragmentManager = getSupportFragmentManager();
    // Email signup.
    if (savedInstanceState == null) {
      switchEmailFragment(null);
    } else {
      currentFragment = fragmentManager.getFragment(savedInstanceState,
          savedInstanceState.getString("currentFragmentKey"));
      setRegisterLoginButtonText();
    }

    // Facebook login.
    mFacebookCallbackManager = CallbackManager.Factory.create();
    LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
        loginResultFacebookCallback);

  }
  
  @Override
  protected void onStart() {
    super.onStart();

    // Check if user is signed in (non-null) and update UI accordingly.
    FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
      updateUI(user);
    }
  }

  private void setRegisterLoginButtonText() {
    Button registerOrLoginButton = findViewById(R.id.register_login);
    if (currentFragment.getTag() == LOGIN_FRAGMENT) {
      registerOrLoginButton.setText(R.string.or_register);
    } else {
      registerOrLoginButton.setText(R.string.or_login);
    }
  }

  public void switchEmailFragment(View view){
    if (currentFragment == null) {
      currentFragment = EmailLoginFragment.newInstance();
      fragmentManager.beginTransaction().add(R.id.account_fragment_container,
          currentFragment, LOGIN_FRAGMENT).commit();
    } else if (currentFragment.getTag() == LOGIN_FRAGMENT) {
      currentFragment = EmailRegisterFragment.newInstance();
      fragmentManager.beginTransaction().replace(R.id.account_fragment_container,
          currentFragment, REGISTER_FRAGMENT).commit();
    } else {
      currentFragment = EmailLoginFragment.newInstance();
      fragmentManager.beginTransaction().replace(R.id.account_fragment_container,
          currentFragment, LOGIN_FRAGMENT).commit();
    }
    setRegisterLoginButtonText();
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
                notRegisteredUser = user;
                requestCamera();
                cardView.setVisibility(View.GONE);
                sendsToQrFragment();
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                Toast.makeText(LoginActivity.this, R.string.register_fail,
                    Toast.LENGTH_SHORT).show();
              }
            }
          });
    } else {
      Toast.makeText(this, R.string.pass_not_matching, Toast.LENGTH_SHORT).show();
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
              updateUI(user);
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithEmail:failure", task.getException());
              Toast.makeText(LoginActivity.this,
                  R.string.wrong_credentials, Toast.LENGTH_SHORT).show();
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
      Toast.makeText(LoginActivity.this, R.string.login_cancelled,
          Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(FacebookException exception) {
      Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
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
    } else {

    }
  }

  private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
    try {
      // Google Sign In was successful, authenticate with Firebase
      GoogleSignInAccount account = task.getResult(ApiException.class);
      Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

      AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
      mAuth.signInWithCredential(credential)
          .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithCredential:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
              } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                updateUI(null);
              }
            }
          });
    } catch (ApiException e) {
      // Google Sign In failed, update UI appropriately
      Log.w(TAG, "Google sign in failed", e);
      updateUI(null);
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
              updateUI(user);
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithCredential:failure", task.getException());
              updateUI(null);
            }
          }
        });
  }

  private void updateUI(FirebaseUser user) {
    if (user == null) {
      Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
    } else {
      DBController db = new DBController(user);
      db.getProfile();
      Intent intent = new Intent(this, MainActivity.class);
      startActivityForResult(intent, LOGOUT);
    }
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

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    // Save the fragment's instance
    fragmentManager.putFragment(outState, currentFragment.getTag(), currentFragment);
    outState.putString("currentFragmentKey", currentFragment.getTag());

  }

  public void sendsToQrFragment(){
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment currFragment = fragmentManager.findFragmentById(R.id.account_fragment_container);
    FragmentTransaction transaction = fragmentManager.beginTransaction();
    transaction.remove(currFragment);
    Fragment fragmentQr = new QrFragment();
    transaction.addToBackStack(null);
    transaction.replace(R.id.qr_fragment_container, fragmentQr);
    transaction.commit();
  }

  private void requestCamera(){
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED){
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }
  }

  @Override
  public void onQRCodeFound(Boolean foundCode) {
      //TODO: validar cuando se regresa sin haber escaneado el QR porque se registra de todas maneras.
      updateUI(notRegisteredUser);
  }
}

