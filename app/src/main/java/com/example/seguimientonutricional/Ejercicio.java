package com.example.seguimientonutricional;

public class Ejercicio extends Registro {

  public Ejercicio() {}

  public Ejercicio(Registro registro) {
    setId(registro.getId());
    setTitulo(registro.getTitulo());
    setDescripcion(registro.getDescripcion());
    setFecha(registro.getFecha());
    setComentario(registro.getComentario());
  }
}
