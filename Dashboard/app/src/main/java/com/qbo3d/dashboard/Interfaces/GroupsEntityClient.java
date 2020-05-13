package com.qbo3d.dashboard.Interfaces;

import com.qbo3d.dashboard.Models.Group;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroupsEntityClient {
    @GET("getGroupsEntity")
    Call<Group[]> getGroupsEntity(
            @Query("user") String user
    );
}
