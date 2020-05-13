package com.qbo3d.dashboard;

import android.os.Environment;

import com.qbo3d.dashboard.Models.Entity;
import com.qbo3d.dashboard.Models.Group;
import com.qbo3d.dashboard.Models.Sensor;
import com.qbo3d.dashboard.Models.Ticket;
import com.qbo3d.dashboard.Models.Usuario;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Vars {

    //URL del servidor donde estan alojados los servicios a consumir
    public static final String urlCI = "http://qbo3d.com/services/sargus/index.php/sargus/";
    public static final String urlGLPI = "https://www.qbo3d.com/systems/sargus/apirest.php/";
    public static final String picturesGLPI = "http://qbo3d.com/systems/sargus/front/document.send.php?file=_pictures/";

    public static String pathMainFolfer = Environment.getExternalStorageDirectory() + File.separator + "Sargus";
    public static String documento = "";
    public static String password = "";

//    public static Bitmap logo = null;

//    Result Codes
    public final static int resCX = 88;
    public final static int resBC = 66;//reult Bar Code

    public static Usuario usuario = null;
    public static Ticket[] allTicket = null;
    public static Entity entidad = null;
    public static Group[] grupos = null;
    public static List<Sensor> sensores = null;
    public static List<List<Sensor>> sensoresLista = null;
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    public static String currentProject;
    public static String currentLocation;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    public final static Retrofit retrofitCI = new Retrofit.Builder().baseUrl(urlCI).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
    public final static Retrofit retrofitGLI = new Retrofit.Builder().baseUrl(urlGLPI).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();

    public static boolean isConn;
}
