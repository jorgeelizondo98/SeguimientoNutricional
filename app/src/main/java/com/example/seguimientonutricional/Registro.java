package com.example.seguimientonutricional;

import android.media.Image;

public class Registro {

    private String mGrade;
    //TODO: ponerlo en tiempo
    private String mTime;
    private Image mImage;


    public Registro(){

    }

    public Registro(String grade, String time){
        mGrade = grade;
        mTime = time;
    }


    public String getGrade(){
        return mGrade;
    }

    public String getTime(){
        return mTime;
    }



}
