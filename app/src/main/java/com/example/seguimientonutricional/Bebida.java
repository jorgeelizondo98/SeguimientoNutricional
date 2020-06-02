package com.example.seguimientonutricional;

public class Bebida extends Registro {

  public Bebida() {}

  private Integer mSodio = -1;
  private Integer mAzucares = -1;
  private Integer mCantidad = -1;

  public Bebida(Registro registro) {
    setId(registro.getId());
    setTitulo(registro.getTitulo());
    setDescripcion(registro.getDescripcion());
    setFecha(registro.getFecha());
    setComentario(registro.getComentario());
  }

  public Integer getSodio() {
    return mSodio;
  }

  public void setmSodio(Integer sodio) {
    this.mSodio = sodio;
  }

  public Integer getAzucares() {
    return mAzucares;
  }

  public void setmAzucares(Integer azucares) {
    this.mAzucares = azucares;
  }

  public Integer getCantidad() {
    return mCantidad;
  }

  public void setmCantidad(Integer cantidad) {
    this.mCantidad = cantidad;
  }
}
