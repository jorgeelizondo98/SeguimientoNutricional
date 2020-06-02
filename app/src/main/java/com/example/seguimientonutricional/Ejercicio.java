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

  public String getHour(){
    String hour = "";
    for (char c : duracion.toCharArray()) {
      if (Character.isWhitespace(c)) {
        break;
      }else{
        hour += c;
      }
    }

    return hour;
  }


  public String getMinutes(){
    String temp;
    if(getHour().length() == 1){
       temp = duracion.substring(7);
    } else {
       temp = duracion.substring(8);
    }
    String minutes = "";
    for (char c :temp.toCharArray()) {
      if (Character.isWhitespace(c)) {
        break;
      }else{
        minutes += c;
      }
    }

    return minutes;
  }
}
