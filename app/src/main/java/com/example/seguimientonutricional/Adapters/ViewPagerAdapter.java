package com.example.seguimientonutricional.Adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

//        switch (position){
//            case 0: return new fragment_
//            case 1: return new fragment_bebidas();
//            case 2: return new fragment_ejercicios();
//            default: return null;
//        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        switch (position){
//            case 0: return "Comidas";
//            case 1: return "Bebidas";
//            case 2: return "Ejercicios";
//            default: return "";
//        }
        return "";
    }

}
