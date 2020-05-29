package com.example.seguimientonutricional.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
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


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);



    }

    public void onResume() {
        super.onResume();
        //You can change preference summary programmatically like following.
        final SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) findPreference("recordatorio");
        switchPreference.setSummaryOff("Switch off state updated from code");
        switchPreference.setSummaryOn("Switch on state updated from code");

        final SwitchPreferenceCompat nutriologo = (SwitchPreferenceCompat) findPreference("nutriologo");

        //You can read preference value anywhere in the app like following.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isChecked = sharedPreferences.getBoolean("recordatorio", false);

        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(switchPreference.isChecked()){
                    Toast.makeText(getActivity(), "isChecked : " + false, Toast.LENGTH_LONG).show();
                    switchPreference.setChecked(false);
                    return true;
                }
                else{
                    Toast.makeText(getActivity(), "isChecked : " + true, Toast.LENGTH_LONG).show();
                    switchPreference.setChecked(true);
                    return false;
                }
            }
        });

        nutriologo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if(nutriologo.isChecked()){
                    Toast.makeText(getActivity(), "isChecked : " + false, Toast.LENGTH_LONG).show();
                    nutriologo.setChecked(false);
                    return true;
                }
                else{
                    Toast.makeText(getActivity(), "isChecked : " + true, Toast.LENGTH_LONG).show();
                    nutriologo.setChecked(true);
                    sendsToQrFragment();
                    return false;
                }

            }
        });



        Fragment fragment = getParentFragment().getChildFragmentManager().findFragmentById(R.id.fragment);
        db = new DBController(fragment);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);
    }

    public void sendsToQrFragment(){


        Fragment fragmentQr = new QrFragment();
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        getChildFragmentManager().beginTransaction()
                .add(R.id.fragment,fragmentQr).addToBackStack(null)
                .commit();
    }

    @Override
    public void onQRCodeFound(String result ) {
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
}