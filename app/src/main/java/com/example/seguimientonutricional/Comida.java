package com.example.seguimientonutricional;

public class Comida extends Registro {

    private Integer mCarbohidratos = -1;
    private Integer mProteinas = -1;
    private Integer mGrasas = -1;
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

    public Comida(String titulo, String descripcion) {
        this.setTitulo(titulo);
        this.setDescripcion(descripcion);
    }

    public Integer getCarbohidratos() { return mCarbohidratos; }

    public void setCarbohidratos(Integer mCarbohidratos) { this.mCarbohidratos = mCarbohidratos; }

    public Integer getProteinas() { return mProteinas; }

    public void setProteinas(Integer mProteinas) { this.mProteinas = mProteinas; }

    public Integer getGrasas() { return mGrasas; }

    public void setGrasas(Integer mGrasas) { this.mGrasas = mGrasas; }

    public String getFotoUrl() {
        return mFotoUrl;
    }

    public void setFotoUrl(String mFotoUrl) {
        this.mFotoUrl = mFotoUrl;
    }
}
