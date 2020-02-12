package com.zamnadev.mwhatsapp.Moldes;

public class Mensaje {

    private String mensaje;
    private long hora;
    private boolean enviado;
    private boolean visto;
    private int tipo;
    private String imagen;

    public Mensaje() {
    }

    public Mensaje(String mensaje, long hora, boolean enviado, boolean visto, int tipo) {
        this.mensaje = mensaje;
        this.hora = hora;
        this.enviado = enviado;
        this.visto = visto;
        this.tipo = tipo;
    }

    public Mensaje(String mensaje, long hora, boolean enviado, boolean visto, int tipo, String imagen) {
        this.mensaje = mensaje;
        this.hora = hora;
        this.enviado = enviado;
        this.visto = visto;
        this.tipo = tipo;
        this.imagen = imagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public long getHora() {
        return hora;
    }

    public void setHora(long hora) {
        this.hora = hora;
    }

    public boolean isEnviado() {
        return enviado;
    }

    public void setEnviado(boolean enviado) {
        this.enviado = enviado;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
}
