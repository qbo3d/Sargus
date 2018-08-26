package com.qbo3d.sargus.Interfaces;

import com.qbo3d.sargus.Objects.Ticket;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AllTicketClient {
    @GET("getAllTicket")
    Call<Ticket[]> getAllTicket(
            @Query("Tecnico") String tecnico);
}
