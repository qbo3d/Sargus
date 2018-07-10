package com.qbo3d.sargus.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qbo3d.sargus.DialogFragment.DialogFragmentVal;
import com.qbo3d.sargus.R;

import java.io.File;

public class Util {

    public static boolean isNetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();
        return (actNetInfo != null && actNetInfo.isConnected());
    }

    public static boolean isOnlineNet() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int val = p.waitFor();
            return (val == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Boolean isConnect(Activity activity, String message) {
        boolean res = false;

        if (Util.isNetAvailable(activity)) {
            res = true;
        } else {
            disconnectMessage(activity, message);
            Log.e("Error Conn", "Red Paila: Activity " + activity.getLocalClassName());
        }

        return  res;
    }

    public static void disconnectMessage(Activity activity, String message){
        android.app.FragmentManager manager = activity.getFragmentManager();
        android.app.Fragment frag = manager.findFragmentByTag(activity.getString(R.string.df_edit_name));
        if (frag != null) {
            manager.beginTransaction().remove(frag).commit();
        }
        DialogFragmentVal editNameDialog;

        editNameDialog = new DialogFragmentVal(Vars.resCX, activity.getString(R.string.login_net_error), activity.getString(R.string.login_net_error_content), "Ok", "");
        editNameDialog.show(manager, activity.getString(R.string.df_edit_name));
        Log.i("WayLog","disconnectMessage: " + message);
    }

    public static void eliminarLogo(){
        if (Vars.usuario != null) {
            File file = new File(Vars.pathMainFolfer);

            borrarDirectorio(file);

            if (file.delete())
                System.out.println("El directorio " + Vars.pathMainFolfer + " ha sido borrado correctamente");
            else
                System.out.println("El directorio " + Vars.pathMainFolfer + " no se ha podido borrar");
        }
    }

    private static void borrarDirectorio(File directorio){
        File[] ficheros = directorio.listFiles();
        for (File fichero : ficheros) {
            if (fichero.isDirectory()) {
                borrarDirectorio(fichero);
            }
            fichero.delete();
        }
    }

    public static void createFolders(Activity act){

        String error = "";

        File fileMainFolfer = new File(Vars.pathMainFolfer);
        try {
            if (!fileMainFolfer.exists()) {
                if (!fileMainFolfer.mkdirs()) {
                    error = "Principal, ";
                }
            }

            if (!error.equals("")) {
                Toast.makeText(act.getApplicationContext(),
                        act.getString(R.string.file_wrong) + " en la creación de las carpetas " + error,
                        Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    public static String objectToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static void setStorageBoolean(Context context, String s, boolean b) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putBoolean(s, b);
        edit.apply();
    }

    public static void setStorageInt(Context context, String s, int i) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putInt(s, i);
        edit.apply();
    }

    public static void setStorageString(Context context, String s, String s1) {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
        edit.putString(s, s1);
        edit.apply();
    }

    public static boolean getStorageBoolean(Context context, String s) {
        return  PreferenceManager.getDefaultSharedPreferences(context).getBoolean(s, false);
    }

    public static String  getStorageString(Context context, String s) {
        return  PreferenceManager.getDefaultSharedPreferences(context).getString(s, "");
    }

    public static int getStorageInt(Context context, String s) {
        return  PreferenceManager.getDefaultSharedPreferences(context).getInt(s, -1);
    }
}
