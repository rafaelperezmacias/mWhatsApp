package com.zamnadev.mwhatsapp.Moldes;

public class Estado {

    private String imagen;
    private long timepoInicio;
    private long tiempoFin;
    private String id;
    private String idUsuario;

    public Estado() {
    }

    public Estado(String imagen, long timepoInicio, long tiempoFin, String id, String idUsuario) {
        this.imagen = imagen;
        this.timepoInicio = timepoInicio;
        this.tiempoFin = tiempoFin;
        this.id = id;
        this.idUsuario = idUsuario;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public long getTimepoInicio() {
        return timepoInicio;
    }

    public void setTimepoInicio(long timepoInicio) {
        this.timepoInicio = timepoInicio;
    }

    public long getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(long tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
