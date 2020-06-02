package com.example.seguimientonutricional;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    private Calendar currFecha;

    DatePickerFragment(Calendar currFecha){
        this.currFecha = currFecha;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        //Initializes date on calendar with the values of the current one selected by user
        int year = currFecha.get(Calendar.YEAR);
        int month = currFecha.get(Calendar.MONTH);
        int day = currFecha.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),
                (DatePickerDialog.OnDateSetListener) getActivity(),year,month ,day);

        Calendar c = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());

        return datePickerDialog;
    }
}
