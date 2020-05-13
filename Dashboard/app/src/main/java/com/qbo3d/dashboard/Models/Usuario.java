package com.qbo3d.dashboard.Models;

public class Usuario {
    private String id;
    private String _id;
    private String documento;
    private String password;
    private String realname;
    private String firstname;
    private String phone;
    private String mobile;
    private String picture;
    private String entidad;
    private String address;
    private String postcode;
    private String town;
    private String state;
    private String country;
    private String website;
    private String phonenumber;
    private String email;

    public Usuario() {
    }

    public Usuario(String id, String _id, String documento, String password, String realname, String firstname, String phone, String mobile, String picture, String entidad, String address, String postcode, String town, String state, String country, String website, String phonenumber, String email) {
        this.id = id;
        this._id = _id;
        this.documento = documento;
        this.password = password;
        this.realname = realname;
        this.firstname = firstname;
        this.phone = phone;
        this.mobile = mobile;
        this.picture = picture;
        this.entidad = entidad;
        this.address = address;
        this.postcode = postcode;
        this.town = town;
        this.state = state;
        this.country = country;
        this.website = website;
        this.phonenumber = phonenumber;
        this.email = email;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return firstname + " " + realname;
    }

    public String getNombreLogo(){
        return picture.split("/")[picture.split("/").length-1];
    }
}
