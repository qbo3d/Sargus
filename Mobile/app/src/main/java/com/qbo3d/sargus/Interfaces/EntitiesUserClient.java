package com.qbo3d.sargus.Interfaces;

import com.qbo3d.sargus.Objects.Entity;
import com.qbo3d.sargus.Objects.Usuario;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EntitiesUserClient {
    @GET("getEntitiesUser")
    Call<Entity> getEntitiesUser(
            @Query("user") String user
    );
}
