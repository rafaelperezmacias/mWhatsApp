package com.zamnadev.mwhatsapp.Notificaciones;

public class Enviar {

    private Data data;
    private String to;

    public Enviar() {
    }

    public Enviar(Data data, String to) {
        this.data = data;
        this.to = to;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
