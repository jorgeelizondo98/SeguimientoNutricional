package com.example.seguimientonutricional;

public class Ejercicio extends Registro {

  private String duracion;
  private Integer intensidad = 1;

  public Ejercicio() {}

  public Ejercicio(Registro registro) {
    setId(registro.getId());
    setTitulo(registro.getTitulo());
    setDescripcion(registro.getDescripcion());
    setFecha(registro.getFecha());
    setComentario(registro.getComentario());
  }

  public String getDuracion() { return duracion; }

  public void setDuracion(String duracion) { this.duracion = duracion; }

  public Integer getIntensidad() { return intensidad; }

  public void setIntensidad(Integer intensidad) { this.intensidad = intensidad; }
}
