package com.example.seguimientonutricional;

public class Registro {

    private String id;
    private String mTitulo;
    private String mDescripcion;
    private String mFecha;
    private String mComentario;

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

    public String getFecha() {
        return mFecha;
    }

    public void setFecha(String mFecha) {
        this.mFecha = mFecha;
    }

    public String getComentario() { return mComentario; }

    public void setComentario(String mComentario) { this.mComentario = mComentario; }
}
