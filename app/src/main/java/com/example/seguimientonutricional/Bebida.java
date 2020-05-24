package com.example.seguimientonutricional;

import com.example.seguimientonutricional.Registro;

public class Bebida extends Registro {

  public Bebida() {}

  public Bebida(Registro registro) {
    setId(registro.getId());
    setTitulo(registro.getTitulo());
    setDescripcion(registro.getDescripcion());
    setFecha(registro.getFecha());
    setComentario(registro.getComentario());
  }
}
