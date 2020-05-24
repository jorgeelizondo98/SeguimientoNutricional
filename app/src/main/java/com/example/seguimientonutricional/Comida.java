package com.example.seguimientonutricional;

public class Comida extends Registro {

    private Integer mCarbohidratos;
    private Integer mProteinas;
    private Integer mGrasas;
    private String mFotoUrl;

    public Comida(){


    }

    public Comida(String id, Integer carbohidratos, Integer proteinas, Integer grasas) {
        mCarbohidratos = carbohidratos;
        mProteinas = proteinas;
        mGrasas = grasas;
    }

    public Comida(Registro registro) {
        setId(registro.getId());
        setTitulo(registro.getTitulo());
        setDescripcion(registro.getDescripcion());
        setFecha(registro.getFecha());
        setComentario(registro.getComentario());
    }

    public Comida(String titulo, String descricion) {
        this.setTitulo(titulo);
        this.setDescripcion(descricion);
    }

    public String getFotoUrl() {
        return mFotoUrl;
    }

    public void setFotoUrl(String mFotoUrl) {
        this.mFotoUrl = mFotoUrl;
    }
}
