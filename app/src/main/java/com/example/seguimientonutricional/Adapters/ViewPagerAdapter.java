package com.example.seguimientonutricional.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.seguimientonutricional.ui.home.tabsFragments.BebidasFragment;
import com.example.seguimientonutricional.ui.home.tabsFragments.ComidasFragment;
import com.example.seguimientonutricional.ui.home.tabsFragments.EjerciciosFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0: return new ComidasFragment();
            case 1: return new BebidasFragment();
            case 2: return new EjerciciosFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "Comidas";
            case 1: return "Bebidas";
            case 2: return "Ejercicios";
            default: return "";
        }
    }

}
