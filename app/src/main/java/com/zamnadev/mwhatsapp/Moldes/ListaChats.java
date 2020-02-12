package com.zamnadev.mwhatsapp.Moldes;

public class ListaChats {

    private String id;
    private Long hora;

    public ListaChats() {
    }

    public ListaChats(String id, Long hora) {
        this.id = id;
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getHora() {
        return hora;
    }

    public void setHora(Long hora) {
        this.hora = hora;
    }
}
