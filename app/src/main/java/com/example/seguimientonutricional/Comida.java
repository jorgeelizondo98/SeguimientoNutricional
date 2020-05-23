package com.example.seguimientonutricional;

public class Comida extends Registro {

    private String mFotoUrl;

    public Comida() {}

    public String getFotoUrl() {
        return mFotoUrl;
    }

    public void setFotoUrl(String mFotoUrl) {
        this.mFotoUrl = mFotoUrl;
    }
}
