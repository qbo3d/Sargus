package com.qbo3d.dashboard.Interfaces;

import com.qbo3d.dashboard.Models.Usuario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LoginClient {
    @GET("getLogin")
    Call<Usuario> getLogin(
            @Query("Documento") String documento,
            @Query("Password") String password
    );
}
