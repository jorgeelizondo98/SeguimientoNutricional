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

    public String getFotoUrl() {
        return mFotoUrl;
    }

    public void setFotoUrl(String mFotoUrl) {
        this.mFotoUrl = mFotoUrl;
    }
}
