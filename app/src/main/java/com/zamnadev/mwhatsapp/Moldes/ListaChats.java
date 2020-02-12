package com.zamnadev.mwhatsapp.Moldes;

public class ListaChats {

    private String id;
    private Long fecha;

    public ListaChats() {
    }

    public ListaChats(String id, Long fecha) {
        this.id = id;
        this.fecha = fecha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }
}
