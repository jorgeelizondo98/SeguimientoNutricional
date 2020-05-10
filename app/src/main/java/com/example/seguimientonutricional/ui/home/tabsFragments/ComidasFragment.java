package com.example.seguimientonutricional.ui.home.tabsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import androidx.fragment.app.Fragment;

import com.example.seguimientonutricional.Adapters.GridViewAdapter;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.Registro;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComidasFragment extends Fragment {

    public ComidasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_comidas, container, false);

        ArrayList<Registro> registros = new ArrayList<Registro>();

        registros.add(new Registro("1/5","4:30 pm"));
        registros.add(new Registro("3/5","8:30 pm"));
        registros.add(new Registro("5/5","9:30 pm"));
        registros.add(new Registro("2/5","10:30 pm"));


        ArrayAdapter<Registro> itemAdapter = new GridViewAdapter(getActivity(), registros);

        GridView gridView = (GridView) root.findViewById(R.id.grid_view_id);

        gridView.setAdapter(itemAdapter);

        return root;
    }
}
