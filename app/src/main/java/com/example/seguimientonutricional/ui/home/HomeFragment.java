package com.example.seguimientonutricional.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.seguimientonutricional.ActividadesFragmentTabs;
import com.example.seguimientonutricional.BebidaFormsFragment;
import com.example.seguimientonutricional.ComidaFormsFragment;
import com.example.seguimientonutricional.EjercicioFormsFragment;
import com.example.seguimientonutricional.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class HomeFragment extends Fragment implements ActividadesFragmentTabs.OnTabSelectedListener {

    private HomeViewModel homeViewModel;
    private FloatingActionButton fabAddButton;
    private String date;
    private Integer currentPosition;



    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final FragmentManager fm  = getActivity().getSupportFragmentManager();
        setUpTabsFragments(fm);

        fabAddButton = root.findViewById(R.id.fab);

        currentPosition = 0;
        fabAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = null;
                String tag = "";

                if(currentPosition == 0){
                    fragment = new ComidaFormsFragment();
                    tag = "comidaForm";

                } else if(currentPosition == 1){
                    fragment = new BebidaFormsFragment();
                    tag = "bebidaForm";
                } else if(currentPosition == 2){
                    fragment = new EjercicioFormsFragment();
                    tag = "ejercicioForm";
                }

                getChildFragmentManager().beginTransaction().replace(R.id.container_home_content,fragment,tag)
                        .commit();
            }
        });
        return root;
    }


    private void setUpTabsFragments(FragmentManager fm){
        Fragment fragmentTabs = new ActividadesFragmentTabs();
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container_home_content,fragmentTabs)
                .commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        final FragmentManager fm  = getActivity().getSupportFragmentManager();
        setUpTabsFragments(fm);
        fabAddButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_calendar);
        if(item!=null)
            item.setVisible(true);
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

    @Override
    public void onTabChanged(int position) {
        currentPosition = position;
    }
}
