package com.example.seguimientonutricional.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.seguimientonutricional.ActividadesFragmentTabs;
import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FloatingActionButton fabAddButton;


    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        final FragmentManager fm  = getActivity().getSupportFragmentManager();
        setUpTabsFragments(fm);


        fabAddButton = root.findViewById(R.id.fab);

        //TODO: Implementar accion del floating button para agregar registro
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fabAddButton.setVisibility(View.GONE);

                Fragment fragment = new ComidaFormsFragment();
                fm.beginTransaction().replace(R.id.container_home_content,fragment)
                        .addToBackStack(null).commit();
            }
        });


        return root;
    }


    private void setUpTabsFragments(FragmentManager fm){
        Fragment fragmentTabs = new ActividadesFragmentTabs();
        fm.beginTransaction().replace(R.id.container_home_content,fragmentTabs).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        final FragmentManager fm  = getActivity().getSupportFragmentManager();
        setUpTabsFragments(fm);
        fabAddButton.setVisibility(View.VISIBLE);
    }
}
