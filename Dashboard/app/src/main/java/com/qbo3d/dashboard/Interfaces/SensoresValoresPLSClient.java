package com.qbo3d.dashboard.Interfaces;

import com.qbo3d.dashboard.Models.Sensor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SensoresValoresPLSClient {
    @GET("getSensoresValoresPLS")
    Call<List<Sensor>> getSensoresValoresPLS(
            @Query("project") String project,
            @Query("sensor") String sensor
    );
}
