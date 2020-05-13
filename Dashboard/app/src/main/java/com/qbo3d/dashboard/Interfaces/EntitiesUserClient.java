package com.qbo3d.dashboard.Interfaces;

import com.qbo3d.dashboard.Models.Entity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EntitiesUserClient {
    @GET("getEntitiesUser")
    Call<Entity> getEntitiesUser(
            @Query("user") String user
    );
}
