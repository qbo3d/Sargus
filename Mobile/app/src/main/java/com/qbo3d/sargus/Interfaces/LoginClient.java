package com.qbo3d.sargus.Interfaces;

import com.qbo3d.sargus.Objects.Usuario;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface LoginClient {
    @GET("getLogin")
    Call<Usuario> getLogin(
            @Query("Documento") String documento,
            @Query("Password") String password
    );
}
