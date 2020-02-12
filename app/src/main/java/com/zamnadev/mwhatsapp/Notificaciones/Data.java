package com.zamnadev.mwhatsapp.Notificaciones;

import com.zamnadev.mwhatsapp.Moldes.Usuario;

public class Data {

    private String titulo;
    private String mensaje;
    private String receptor;
    private String emisor;

    public Data() {
    }

    public Data(String titulo, String mensaje, String receptor, String emisor) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.receptor = receptor;
        this.emisor = emisor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getReceptor() {
        return receptor;
    }

    public void setReceptor(String receptor) {
        this.receptor = receptor;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }
}
