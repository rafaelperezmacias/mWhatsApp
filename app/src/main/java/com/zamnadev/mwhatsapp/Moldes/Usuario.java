package com.zamnadev.mwhatsapp.Moldes;

public class Usuario {

    private String id;
    private String correo;
    private String nombre;
    private String imagen;
    private long hora;
    private boolean conectado;

    public Usuario() {
    }

    public Usuario(String id, String correo, String nombre, String imagen, long hora, boolean conectado) {
        this.id = id;
        this.correo = correo;
        this.nombre = nombre;
        this.imagen = imagen;
        this.hora = hora;
        this.conectado = conectado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
}
