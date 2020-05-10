package com.example.seguimientonutricional.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.Registro;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<Registro> {

    public GridViewAdapter(Activity context, ArrayList<Registro> registros){
        super(context, 0 ,registros);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridItemView = convertView;

        if(gridItemView ==  null){
            gridItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Registro currRegistro = getItem(position);

        TextView gradeTextView = gridItemView.findViewById(R.id.grade_id);
        gradeTextView.setText(currRegistro.getGrade());

        TextView timeTextView = gridItemView.findViewById(R.id.time_id);
        timeTextView.setText(currRegistro.getTime());


        return gridItemView;
    }
}
