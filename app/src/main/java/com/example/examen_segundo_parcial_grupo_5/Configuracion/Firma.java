package com.example.examen_segundo_parcial_grupo_5.Configuracion;

import android.graphics.Bitmap;

public class Firma {

    private String id;
    private String nombre;
    private String telefono;
    private String latitud;
    private  String longitud;
    private Bitmap firma;

    private String conver64;

    public Firma(String id, String nombre, String telefono, String latitud, String longitud, Bitmap firma,String conver64) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
        this.firma = firma;
        this.conver64=conver64;
    }

    public String getConver64() {
        return conver64;
    }

    public void setConver64(String conver64) {
        this.conver64 = conver64;
    }

    public Firma() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Bitmap getFirma() {
        return firma;
    }

    public void setFirma(Bitmap firma) {
        this.firma = firma;
    }
}
