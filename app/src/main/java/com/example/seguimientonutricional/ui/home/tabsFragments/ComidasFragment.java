package com.example.seguimientonutricional.ui.home.tabsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seguimientonutricional.Adapters.AdapterComida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComidasFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ArrayList<Comida> mComidas;
    private AdapterComida adapterComida;


    public ComidasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_comidas, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view_id);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));

        mComidas = new ArrayList<Comida>();


        adapterComida = new AdapterComida(getActivity(),mComidas);
        mRecyclerView.setAdapter(adapterComida);



        return root;
    }
}
