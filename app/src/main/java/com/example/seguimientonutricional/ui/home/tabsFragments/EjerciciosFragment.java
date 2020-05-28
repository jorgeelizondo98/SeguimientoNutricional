package com.example.seguimientonutricional.ui.home.tabsFragments;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seguimientonutricional.Adapters.AdapterEjercicio;
import com.example.seguimientonutricional.Bebida;
import com.example.seguimientonutricional.Comida;
import com.example.seguimientonutricional.DBController;
import com.example.seguimientonutricional.Ejercicio;
import com.example.seguimientonutricional.FragmentLifeCycle;
import com.example.seguimientonutricional.Profile;
import com.example.seguimientonutricional.R;
import com.example.seguimientonutricional.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EjerciciosFragment extends Fragment implements DBController.DBResponseListener,
        FragmentLifeCycle {

    private RecyclerView mRecyclerView;
    private ArrayList<Ejercicio> mEjercicio;
    private AdapterEjercicio adapterEjercicio;
    private HomeViewModel homeViewModel;
    private Date fecha;
    private DBController db;
    private Profile profile;

    public EjerciciosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_ejercicios, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view_id);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        mEjercicio = new ArrayList<Ejercicio>();
        makeGridViewDynamic();

        List<Fragment> allFragments = getParentFragment().getChildFragmentManager().getFragments();

        //We get the actual fragment running
        for (Fragment fragmento: allFragments) {
            if (fragmento instanceof EjerciciosFragment){
                db = new DBController(fragmento);
            }
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);
        fecha = Calendar.getInstance().getTime();

        return root;
    }

    private void makeGridViewDynamic(){
        if(mRecyclerView != null){
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            }
            else{
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setAdapterEjercicio(){
        adapterEjercicio = new AdapterEjercicio(getActivity(),mEjercicio,getParentFragment().getParentFragment());
        mRecyclerView.setAdapter(adapterEjercicio);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadsEjercicio(){
        db.loadEjercicios(profile,fecha);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        ViewModelProviders.of(getActivity()).get(HomeViewModel.class).getDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(@Nullable Date newDate) {
                fecha = newDate;
                if(profile != null){
                    loadsEjercicio();
                    setAdapterEjercicio();
                }
            }
        });
    }

    @Override
    public void onDatabaseNetworkError() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onProfileReceived(Profile profile) throws ParseException {
        this.profile = profile;
        loadsEjercicio();
        setAdapterEjercicio();
    }

    @Override
    public void onComidasReceived(ArrayList<Comida> comidas) {

    }

    @Override
    public void onBebidasReceived(ArrayList<Bebida> bebidas) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios) {
        mEjercicio = ejercicios;
        setAdapterEjercicio();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPauseFragment() {
        if(profile != null){
            setAdapterEjercicio();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResumeFragment() {
        if(profile != null){
            setAdapterEjercicio();
        }
    }
}
