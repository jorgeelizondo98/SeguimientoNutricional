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

import com.example.seguimientonutricional.Adapters.AdapterBebida;
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
public class BebidasFragment extends Fragment implements DBController.DBResponseListener,
        FragmentLifeCycle {

    private RecyclerView mRecyclerView;
    private ArrayList<Bebida> mBebidas;
    private AdapterBebida adapterBebida;
    private HomeViewModel homeViewModel;
    private Date fecha;
    private DBController db;
    private Profile profile;

    public BebidasFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_bebidas, container, false);
        mRecyclerView = root.findViewById(R.id.recycler_view_id);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        mBebidas = new ArrayList<Bebida>();
        makeGridViewDynamic();

        List<Fragment> allFragments = getParentFragment().getChildFragmentManager().getFragments();

        //We get the actual fragment running
        for (Fragment fragmento: allFragments) {
            if (fragmento instanceof BebidasFragment){
                db = new DBController(fragmento);
            }
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db.loadProfile(currentUser);
        fecha = Calendar.getInstance().getTime();

        if(profile !=  null){
            loadsBebida();
            setAdapterBebida();
        }


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
    private void setAdapterBebida(){
        adapterBebida = new AdapterBebida(getActivity(),mBebidas,getParentFragment().getParentFragment());
        mRecyclerView.setAdapter(adapterBebida);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadsBebida(){
        db.loadBebidas(profile,fecha);
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
                    loadsBebida();
                    setAdapterBebida();
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
        loadsBebida();
        setAdapterBebida();
    }

    @Override
    public void onComidasReceived(ArrayList<Comida> comidas) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBebidasReceived(ArrayList<Bebida> bebidas) {
        mBebidas = bebidas;
        setAdapterBebida();
    }

    @Override
    public void onEjerciciosReceived(ArrayList<Ejercicio> ejercicios) {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPauseFragment() {
        if(profile != null){
            setAdapterBebida();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResumeFragment() {
        if(profile != null){
            setAdapterBebida();
        }
    }
}
