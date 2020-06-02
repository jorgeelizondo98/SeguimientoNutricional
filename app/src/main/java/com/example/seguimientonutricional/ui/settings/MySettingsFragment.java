package com.example.seguimientonutricional.ui.settings;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.Profile;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.ui.account.QrFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MySettingsFragment extends PreferenceFragmentCompat implements QrFragment.OnQrFragmentInteractionListener,
        DBController.DBResponseListener {

    private DBController db;
    private Profile profile;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 200;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

    public void onResume() {
        super.onResume();


        final SwitchPreferenceCompat nutriologo = (SwitchPreferenceCompat) findPreference("nutriologo");

        //You can read preference value anywhere in the app like following.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isChecked = sharedPreferences.getBoolean("recordatorio", false);

        //switch de recordatorio
        final SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) findPreference("recordatorio");
        switchPreference.setSummaryOn("Desactiva recordatorio diario");
        switchPreference.setSummaryOff("Activa recordatorio diario");


        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (switchPreference.isChecked()) {

                    //Cancel alarm is switch off
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
                    alarmManager.cancel(pendingIntent);
                    switchPreference.setChecked(false);
                    return true;
                } else {
                    //settingsNotificationFragment->set alarm
                    final FragmentManager fm = getActivity().getSupportFragmentManager();
                    final Fragment fragment = new SettingsNotificationFragment();
                    fm.beginTransaction().replace(R.id.settings_fragment, fragment)
                            .addToBackStack(null).commit();
                    switchPreference.setChecked(true);
                    return false;
                }
            }
        });


        nutriologo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (nutriologo.isChecked()) {
                    Toast.makeText(getActivity(), "isChecked : " + false, Toast.LENGTH_LONG).show();
                    nutriologo.setChecked(false);
                    return true;
                } else {
                    Toast.makeText(getActivity(), "isChecked : " + true, Toast.LENGTH_LONG).show();
                    nutriologo.setChecked(true);
                    sendsToQrFragment();
                    return false;
                }

            }
        });


        Fragment fragment = getParentFragment().getChildFragmentManager().findFragmentById(R.id.settings_fragment);
        db = new DBController(fragment);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void sendsToQrFragment() {

        if(hasCameraPermission()){
            Fragment fragmentQr = new QrFragment();
            List<Fragment> fragments = getChildFragmentManager().getFragments();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.settings_fragment, fragmentQr).addToBackStack(null)
                    .commit();
        } else {
            requestCamera();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasCameraPermission() {
        if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else
            return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCamera(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
        sendsToQrFragment();
    }

    @Override
    public void onQRCodeFound(String result) {
        if (profile != null) {
            db.associateDoctor(profile, result);
        }
    }

    @Override
    public void onDatabaseNetworkError() {

    }

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
}