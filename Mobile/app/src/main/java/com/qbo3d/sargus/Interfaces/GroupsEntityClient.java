package com.qbo3d.sargus.Interfaces;

import com.qbo3d.sargus.Objects.Group;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroupsEntityClient {
    @GET("getGroupsEntity")
    Call<Group[]> getGroupsEntity(
            @Query("user") String user
    );
}
