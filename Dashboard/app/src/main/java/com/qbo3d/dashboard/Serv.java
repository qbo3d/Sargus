package com.qbo3d.dashboard;

import android.app.Activity;

import com.qbo3d.dashboard.Interfaces.AllTicketClient;
import com.qbo3d.dashboard.Interfaces.EntitiesUserClient;
import com.qbo3d.dashboard.Interfaces.GroupsEntityClient;
import com.qbo3d.dashboard.Interfaces.LoginClient;
import com.qbo3d.dashboard.Interfaces.SensoresValoresPLClient;
import com.qbo3d.dashboard.Interfaces.SensoresValoresPLSClient;
import com.qbo3d.dashboard.Models.Entity;
import com.qbo3d.dashboard.Models.Group;
import com.qbo3d.dashboard.Models.Sensor;
import com.qbo3d.dashboard.Models.Ticket;
import com.qbo3d.dashboard.Models.Usuario;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class Serv {

    /** Función que consume el servicio UsuarioA.
     *
     * @param documento lo que recibe
     * @param password lo que recibe
     */
    public static boolean getLogin(Activity activity, String documento, String password){
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

    /** Función que consume el servicio getSensoresValoresPL.
     *
     */
    public static boolean getSensoresValoresPL(String project){
        boolean res = false;
        SensoresValoresPLClient client = Vars.retrofitCI.create(SensoresValoresPLClient.class);
        Call<List<Sensor>> call = client.getSensoresValoresPLC(project);

        try {
            Response<List<Sensor>> response = call.execute();
            if (response.code() == 200) {
                Vars.sensores = response.body();
                res = true;
            } else {
                res = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    /** Función que consume el servicio getSensoresValoresPL.
     *
     */
    public static List<Sensor> getSensoresValoresPLS(String project, String sensor){
        List<Sensor> res = null;
        SensoresValoresPLSClient client = Vars.retrofitCI.create(SensoresValoresPLSClient.class);
        Call<List<Sensor>> call = client.getSensoresValoresPLS(project, sensor);

        try {
            Response<List<Sensor>> response = call.execute();
            if (response.code() == 200) {
                res = response.body();
            } else {
                res = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

}
