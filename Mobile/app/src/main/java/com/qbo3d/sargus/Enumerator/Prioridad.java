package com.qbo3d.sargus.Enumerator;

import com.qbo3d.sargus.R;

public enum Prioridad {

    Prioridad_1 (1, "Muy baja", R.color.rojo1),
    Prioridad_2 (2, "Baja", R.color.rojo2),
    Prioridad_3 (3, "Media", R.color.rojo3),
    Prioridad_4 (4, "Alta", R.color.rojo4),
    Prioridad_5 (5, "Muy Alta", R.color.rojo5),
    Prioridad_6 (6, "Primordial", R.color.rojo6);

    private int id;
    private String prioridad;
    private int color;

    Prioridad(int id, String prioridad, int color) {
        this.id = id;
        this.prioridad = prioridad;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
