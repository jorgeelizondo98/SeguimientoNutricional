package com.example.seguimientonutricional.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.TimePickerFragment;

public class SettingsNotificationFragment extends Fragment implements TimePickerFragment.OnTimeDialogListener {

    EditText ed_setTime;
    Button b_save;
    Button b_cancel;

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
        //b_cancel = root.findViewById(R.id.cancelButton);

        ed_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.setTargetFragment(SettingsNotificationFragment.this,1);
                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
            }

        });

        


        return root;
    }



        @Override
    public void onTimeSet(int hour, int minute) {
            ed_setTime.setText(Integer.toString(hour)+":"+Integer.toString(minute));
    }
}
