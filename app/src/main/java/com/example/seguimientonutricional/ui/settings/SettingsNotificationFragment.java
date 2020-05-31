package com.example.seguimientonutricional.ui.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class SettingsNotificationFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener{

    private EditText ed_setTime;
    private Button b_save;
    private Context mContext;
    private TextView mTextView;

    public SettingsNotificationFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings_notifications, container, false);

        ed_setTime = root.findViewById(R.id.ed_setTime);
        b_save = root.findViewById(R.id.save_button);
        mTextView = root.findViewById(R.id.tv_timer);
        b_save = root.findViewById(R.id.save_button);
        mContext=getContext();

        ed_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(SettingsNotificationFragment.this,1);
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");

            }

        });


        b_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FragmentManager fm  = getActivity().getSupportFragmentManager();
                final Fragment fragment = new MySettingsFragment();
                fm.beginTransaction().replace(R.id.settings_fragment,fragment)
                        .addToBackStack(null).commit();
            }
        });

        return root;
    }


    //create calendar type
        @Override
    public void onTimeSet(int hour, int minute) {
            ed_setTime.setText(Integer.toString(hour)+":"+Integer.toString(minute));
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            updateTimeText(c);
            startAlarm(c);
    }


    private void updateTimeText(Calendar c) {
        String timeText = "Recordatorio programado para: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        mTextView.setText(timeText);
    }

    //set daily alarm
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(mContext, "Recordatorio guardado", Toast.LENGTH_SHORT).show();;
    }

}
