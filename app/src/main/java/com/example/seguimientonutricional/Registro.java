package com.example.seguimientonutricional;

import java.util.Date;

public class Registro {

    private String id;
    private String mTitulo;
    private String mDescripcion;
    private Date mFecha;
    private String mComentario;

    public Registro() {
        mFecha = new Date();
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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

    public Date getFecha() {
        return mFecha;
    }

    public void setFecha(Date mFecha) {
        this.mFecha = mFecha;
    }

    public String getComentario() { return mComentario; }

    public void setComentario(String mComentario) { this.mComentario = mComentario; }
}
