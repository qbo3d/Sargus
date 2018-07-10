package com.qbo3d.sargus.Utilities;

import android.app.Activity;
import android.util.Log;

import com.qbo3d.sargus.Interfaces.LoginClient;
import com.qbo3d.sargus.Objects.Usuario;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
        LoginClient client = Vars.retrofit.create(LoginClient.class);
        Map<String, String> params = new HashMap<>();
        params.put("Documento", documento);
        params.put("Password", password);
        Call<Usuario> call = client.getLogin(params);

        try {
            Response<Usuario> response = call.execute();
            if (response.code() == 200) {
                Vars.usuario = response.body();
                Util.setStorageString(activity,"objeto_login", Util.objectToJson(Vars.usuario));
                Log.d("Servicio", "onHandleIntent: Respuesta" + response.message());
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
