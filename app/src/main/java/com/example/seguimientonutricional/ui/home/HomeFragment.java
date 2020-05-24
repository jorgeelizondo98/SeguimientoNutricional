package com.example.seguimientonutricional.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.ActividadesFragmentTabs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FloatingActionButton fabAddButton;
    private String date;



    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);


        final FragmentManager fm  = getActivity().getSupportFragmentManager();
        setUpTabsFragments(fm);


        fabAddButton = root.findViewById(R.id.fab);

        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


    private String formatDate(Date dateTime){
        SimpleDateFormat format = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy",
                new Locale("es","MEX"));
        String date = format.format(dateTime);
        return date;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ViewModelProviders.of(getActivity()).get(HomeViewModel.class).getDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date newDate) {
                String date = formatDate(newDate);
                Log.d("checa",date);
            }
        });
    }


}
