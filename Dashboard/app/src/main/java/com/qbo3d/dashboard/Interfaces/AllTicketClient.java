package com.qbo3d.dashboard.Interfaces;

import com.qbo3d.dashboard.Models.Ticket;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AllTicketClient {
    @GET("getAllTicket")
    Call<Ticket[]> getAllTicket(
            @Query("Tecnico") String tecnico);
}
