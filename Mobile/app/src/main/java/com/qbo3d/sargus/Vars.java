package com.qbo3d.sargus;

import android.os.Environment;

import com.qbo3d.sargus.Objects.Entity;
import com.qbo3d.sargus.Objects.Group;
import com.qbo3d.sargus.Objects.Ticket;
import com.qbo3d.sargus.Objects.Usuario;

import java.io.File;
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
    public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    public final static Retrofit retrofitCI = new Retrofit.Builder().baseUrl(urlCI).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
    public final static Retrofit retrofitGLI = new Retrofit.Builder().baseUrl(urlGLPI).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();

    public static boolean isConn;
}
