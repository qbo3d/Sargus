package com.qbo3d.sargus.Enumerator;

import com.qbo3d.sargus.R;

public enum Status {

    Status_1 (1, "Nuevo", R.drawable.shape_circle_verde),
    Status_2 (2, "En curso (asignada)", R.drawable.shape_arandela),
    Status_3 (3, "En curso (planificada)", R.drawable.ic_calendar_today_black_24),
    Status_4 (4, "En espera", R.drawable.shape_circle_amarillo),
    Status_5 (5, "Resuelto", R.drawable.shape_circle_verde),
    Status_6 (6, "Cerrado",R.drawable.shape_circle_verde);

    private int id;
    private String status;
    private int drawable;

    Status(int id, String status, int drawable) {
        this.id = id;
        this.status = status;
        this.drawable = drawable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }
}
