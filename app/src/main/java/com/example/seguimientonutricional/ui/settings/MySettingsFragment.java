package com.example.seguimientonutricional.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

public class MySettingsFragment extends PreferenceFragmentCompat  {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    public void onResume() {
        super.onResume();

        //switch de recordatorio
        final SwitchPreferenceCompat switchPreference = (SwitchPreferenceCompat) findPreference("recordatorio");
        switchPreference.setSummaryOn("Desactiva recordatorio diario");
        switchPreference.setSummaryOff("Activa recordatorio diario");
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if(switchPreference.isChecked()){

                    //Cancel alarm is switch off
                    AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(getContext(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
                    alarmManager.cancel(pendingIntent);
                    switchPreference.setChecked(false);
                    return true;
                }
                else{
                    //settingsNotificationFragment->set alarm
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