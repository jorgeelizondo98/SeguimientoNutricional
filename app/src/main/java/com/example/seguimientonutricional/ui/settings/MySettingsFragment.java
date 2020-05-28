package com.example.seguimientonutricional.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.TimePickerFragment;

public class MySettingsFragment extends PreferenceFragmentCompat {


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


                    final FragmentManager fm  = getActivity().getSupportFragmentManager();
                    final Fragment fragment = new SettingsNotificationFragment();
                    fm.beginTransaction().replace(R.id.settings_fragment,fragment)
                            .addToBackStack(null).commit();

                    switchPreference.setChecked(true);
                    return false;
                }
            }
        });

    }

}