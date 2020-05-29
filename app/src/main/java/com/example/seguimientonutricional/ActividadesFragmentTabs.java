package com.example.seguimientonutricional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.example.seguimientonutricional.Adapters.ViewPagerAdapter;
import com.example.seguimientonutricional.ui.home.HomeViewModel;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ActividadesFragmentTabs extends Fragment  {

    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private HomeViewModel homeViewModel;
    private Integer currentPage = 0;

    private TextView fecha;

    public ActividadesFragmentTabs() {
        // Required empty public constructor
    }

    public interface OnTabSelectedListener{
        public void onTabChanged(int position);
    }

    OnTabSelectedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_actividades_tabs, container, false);

        viewPager = root.findViewById(R.id.pager);
        tabLayout = root.findViewById(R.id.tab_layout);
        //Agrega viewPagerAdapter al tablayout
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);

        Fragment fragment = getParentFragment();
        mListener = (OnTabSelectedListener) fragment;

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position) {
                FragmentLifeCycle fragmentToShow = (FragmentLifeCycle)viewPagerAdapter
                        .getItem(position);
                fragmentToShow.onResumeFragment();

                FragmentLifeCycle fragmentToHide = (FragmentLifeCycle)viewPagerAdapter
                        .getItem(currentPage);
                fragmentToHide.onPauseFragment();

                currentPage = position;
                mListener.onTabChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fecha = root.findViewById(R.id.date_text);
        Date currentDate = Calendar.getInstance().getTime();
        fecha.setText(formatDate(currentDate));

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        ViewModelProviders.of(getActivity()).get(HomeViewModel.class).getDate().observe(getViewLifecycleOwner(), new Observer<Date>() {
            @Override
            public void onChanged(@Nullable Date newDate) {
               fecha.setText(formatDate(newDate));
            }
        });
    }

    private String formatDate(Date dateTime){
        SimpleDateFormat format = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy",
                new Locale("es","MEX"));
        String date = format.format(dateTime);
        return date;
    }

}
