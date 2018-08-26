package com.qbo3d.sargus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qbo3d.sargus.DialogFragment.DialogFragmentVal;
import com.qbo3d.sargus.Enumerator.Prioridad;
import com.qbo3d.sargus.Enumerator.Status;
import com.qbo3d.sargus.Objects.Group;
import com.qbo3d.sargus.Objects.Ticket;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedList;

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

//    public static void eliminarLogo(){
//        if (usuario != null) {
//            File file = new File(pathMainFolfer);
//
//            borrarDirectorio(file);
//
//            if (file.delete())
//                System.out.println("El directorio " + pathMainFolfer + " ha sido borrado correctamente");
//            else
//                System.out.println("El directorio " + pathMainFolfer + " no se ha podido borrar");
//        }
//    }

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

    /** Función que crea una lista con las ordenes de servicio activas.
     *
     * @return listado con los id de las ordenes de servicio
     */
    public static LinkedList<Ticket> ticketListToData() {
        LinkedList<Ticket> data = new LinkedList<>();

        data.addAll(Arrays.asList(Vars.allTicket));

        return data;
    }

    /** Función que crea una lista con las ordenes de servicio activas.
     *
     * @return listado con los id de las ordenes de servicio
     */
    public static LinkedList<Group> groupListToData() {
        LinkedList<Group> data = new LinkedList<>();

        data.addAll(Arrays.asList(Vars.grupos));

        return data;
    }

    public static void cargarAllTickets(final Activity activity, ListView listview, final int resource, final LinkedList<Ticket> data){
        BaseAdapter adapter = new BaseAdapter() {

            @SuppressLint("NewApi")
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                convertView = LayoutInflater.from(activity).inflate(resource, null);

                TextView tv_ilt_id = convertView.findViewById(R.id.tv_ilt_id);
                TextView tv_ilt_prioridad = convertView.findViewById(R.id.tv_ilt_prioridad);
                TextView tv_ilt_estado = convertView.findViewById(R.id.tv_ilt_estado);
                ImageView iv_ilt_estado = convertView.findViewById(R.id.iv_ilt_estado);
                TextView tv_ilt_titulo = convertView.findViewById(R.id.tv_ilt_titulo);
                TextView tv_ilt_categoria = convertView.findViewById(R.id.tv_ilt_categoria);
                TextView tv_ilt_apertura = convertView.findViewById(R.id.tv_ilt_apertura);
                TextView tv_ilt_estimado = convertView.findViewById(R.id.tv_ilt_estimado);
                TextView tv_ilt_solicitante = convertView.findViewById(R.id.tv_ilt_solicitante);
                TextView tv_ilt_asignado = convertView.findViewById(R.id.tv_ilt_asignado);
//                TextView tv_ilt_descripcion = convertView.findViewById(R.id.tv_ilt_descripcion);

                tv_ilt_id.setText("Ticket #" + String.valueOf(new DecimalFormat("000").format(data.get(position).getIdInt())));

                switch (data.get(position).getPriority()){
                    case "1":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_1.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_1.getColor()));
                        break;
                    case "2":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_2.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_2.getColor()));
                        break;
                    case "3":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_3.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_3.getColor()));
                        break;
                    case "4":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_4.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_4.getColor()));
                        break;
                    case "5":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_5.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_5.getColor()));
                        break;
                    case "6":
                        tv_ilt_prioridad.setText("Prioridad " + Prioridad.Prioridad_6.getPrioridad());
                        tv_ilt_prioridad.setBackgroundColor(activity.getColor(Prioridad.Prioridad_6.getColor()));
                        break;
                }

                switch (data.get(position).getStatus()){
                    case "1":
                        tv_ilt_estado.setText(Status.Status_1.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_1.getDrawable()));
                        break;
                    case "2":
                        tv_ilt_estado.setText(Status.Status_2.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_2.getDrawable()));
                        break;
                    case "3":
                        tv_ilt_estado.setText(Status.Status_3.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_3.getDrawable()));
                        break;
                    case "4":
                        tv_ilt_estado.setText(Status.Status_4.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_4.getDrawable()));
                        break;
                    case "5":
                        tv_ilt_estado.setText(Status.Status_5.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_5.getDrawable()));
                        break;
                    case "6":
                        tv_ilt_estado.setText(Status.Status_6.getStatus());
                        iv_ilt_estado.setImageDrawable(activity.getDrawable(Status.Status_6.getDrawable()));
                        break;
                }

                tv_ilt_titulo.setText(data.get(position).getTicket());
                tv_ilt_categoria.setText(data.get(position).getCategory());
                tv_ilt_apertura.setText(data.get(position).getDate());
                tv_ilt_estimado.setText(data.get(position).getTime_to_resolve());
                tv_ilt_solicitante.setText(data.get(position).getSolicitante());
                tv_ilt_asignado.setText(data.get(position).getTecnico());
//                tv_ilt_descripcion.setText(data.get(position).getContent());

                return convertView;
            }

            @Override
            public int getCount() {
                return data.size();
            }

            @Override
            public Object getItem(int position) {
                return data.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }
        };

        listview.setAdapter(adapter);
    }

    public static void cargarGroups(final Activity activity, Spinner spinner, final LinkedList<Group> data){
        ArrayAdapter comboAdapter = new ArrayAdapter<>(activity,android.R.layout.simple_spinner_item, data);
        spinner.setAdapter(comboAdapter);
    }
}
