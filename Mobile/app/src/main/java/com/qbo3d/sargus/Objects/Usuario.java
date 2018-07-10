package com.qbo3d.sargus.Objects;

public class Usuario {
    private String id;
    private String _id;
    private String documento;
    private String password;
    private String realname;
    private String firstname;
    private String picture;

    public Usuario() {
    }

    public Usuario(String id, String _id, String documento, String password, String realname, String firstname, String picture) {
        this.id = id;
        this._id = _id;
        this.documento = documento;
        this.password = password;
        this.realname = realname;
        this.firstname = firstname;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
