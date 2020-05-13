package com.qbo3d.dashboard.Models;

public class Sensor {
    private String id;
    private String project;
    private String location;
    private String sensor;
    private String date;
    private String value1;

    public Sensor() {
    }

    public Sensor(String id, String project, String location, String sensor, String date, String value1) {
        this.id = id;
        this.project = project;
        this.location = location;
        this.sensor = sensor;
        this.date = date;
        this.value1 = value1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensor() {
        return sensor;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSensor(String sensor) {
        this.sensor = sensor;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }
}
