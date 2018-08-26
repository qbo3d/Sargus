package com.qbo3d.sargus.Utilities;

import android.app.Activity;

import com.qbo3d.sargus.Interfaces.AllTicketClient;
import com.qbo3d.sargus.Interfaces.EntitiesUserClient;
import com.qbo3d.sargus.Interfaces.GroupsEntityClient;
import com.qbo3d.sargus.Interfaces.LoginClient;
import com.qbo3d.sargus.Objects.Entity;
import com.qbo3d.sargus.Objects.Group;
import com.qbo3d.sargus.Objects.Ticket;
import com.qbo3d.sargus.Objects.Usuario;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Vars;

import java.io.IOException  ;

import retrofit2.Call;
import retrofit2.Response;

public class Serv {

    /** Función que consume el servicio UsuarioA.
     *
     * @param documento lo que recibe
     * @param password lo que recibe
     */
    public static boolean getLogin(Activity activity, String documento, String password, String id){
        boolean res = false;
        LoginClient client = Vars.retrofitCI.create(LoginClient.class);
        Call<Usuario> call = client.getLogin(documento, password);

        try {
            Response<Usuario> response = call.execute();
            if (response.code() == 200) {
                Vars.usuario = response.body();
                Util.setStorageString(activity,"objeto_login", Util.objectToJson(Vars.usuario));
                res = true;
            } else {
                res = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /** Función que consume el servicio UsuarioA.
     *
     */
    public static boolean getAllTicket(){
        boolean res = false;
        AllTicketClient client = Vars.retrofitCI.create(AllTicketClient.class);
        Call<Ticket[]> call = client.getAllTicket(Vars.usuario.getId());

        try {
            Response<Ticket[]> response = call.execute();
            if (response.code() == 200) {
                Vars.allTicket = response.body();
                res = true;
            } else {
                res = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /** Función que consume el servicio UsuarioA.
     *
     */
    public static boolean getEntitiesUser(){
        boolean res = false;
        EntitiesUserClient client = Vars.retrofitCI.create(EntitiesUserClient.class);
        Call<Entity> call = client.getEntitiesUser(Vars.usuario.getDocumento());

        try {
            Response<Entity> response = call.execute();
            if (response.code() == 200) {
                Vars.entidad = response.body();
                res = true;
            } else {
                res = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /** Función que consume el servicio UsuarioA.
     *
     */
    public static boolean getGroupsEntity(){
        boolean res = false;
        GroupsEntityClient client = Vars.retrofitCI.create(GroupsEntityClient.class);
        Call<Group[]> call = client.getGroupsEntity(Vars.usuario.getDocumento());

        try {
            Response<Group[]> response = call.execute();
            if (response.code() == 200) {
                Vars.grupos = response.body();
                res = true;
            } else {
                res = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

}
