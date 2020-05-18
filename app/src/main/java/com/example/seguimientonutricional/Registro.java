package com.example.seguimientonutricional;

import android.widget.ImageView;

public class Registro {

    private String mTitulo;
    private String mDescripcion;
    private String mFecha;
    private ImageView mFoto;


    public String getTitulo() {
        return mTitulo;
    }

    public void setTitulo(String mTitulo) {
        this.mTitulo = mTitulo;
    }

    public String getDescripcion() {
        return mDescripcion;
    }

    public void setDescripcion(String mDescripcion) {
        this.mDescripcion = mDescripcion;
    }

    public String getFecha() {
        return mFecha;
    }

    public void setFecha(String mFecha) {
        this.mFecha = mFecha;
    }

    public ImageView getFoto() {
        return mFoto;
    }

    public void setFoto(ImageView mFoto) {
        this.mFoto = mFoto;
    }
}
