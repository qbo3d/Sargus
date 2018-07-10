package com.qbo3d.sargus.Interfaces;

import com.qbo3d.sargus.Objects.Usuario;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface LoginClient {
    @GET("getLogin")
    Call<Usuario> getLogin(
            @QueryMap Map<String, String> params
    );
}
