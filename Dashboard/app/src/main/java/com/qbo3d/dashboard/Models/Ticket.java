package com.qbo3d.dashboard.Models;

public class Ticket {

    private String id;
    private String ticket;
    private String status;
    private String date_mod;
    private String date;
    private String priority;
    private String solicitante;
    private String tecnico;
    private String category;
    private String time_to_resolve;
    private String content;

    public Ticket() {
    }

    public Ticket(String id, String ticket, String status, String date_mod, String date, String priority, String solicitante, String tecnico, String category, String time_to_resolve, String content) {
        this.id = id;
        this.ticket = ticket;
        this.status = status;
        this.date_mod = date_mod;
        this.date = date;
        this.priority = priority;
        this.solicitante = solicitante;
        this.tecnico = tecnico;
        this.category = category;
        this.time_to_resolve = time_to_resolve;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public int getIdInt() {
        return Integer.parseInt(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_mod() {
        return date_mod;
    }

    public void setDate_mod(String date_mod) {
        this.date_mod = date_mod;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getTecnico() {
        return tecnico;
    }

    public void setTecnico(String tecnico) {
        this.tecnico = tecnico;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTime_to_resolve() {
        return time_to_resolve;
    }

    public void setTime_to_resolve(String time_to_resolve) {
        this.time_to_resolve = time_to_resolve;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
