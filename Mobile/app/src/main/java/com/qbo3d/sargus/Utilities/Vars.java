package com.qbo3d.sargus.Utilities;

import android.graphics.Bitmap;
import android.os.Environment;

import com.qbo3d.sargus.Objects.Usuario;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Vars {

    //URL del servidor donde estan alojados los servicios a consumir
    public static final String url = "http://qbo3d.com/services/sargus/index.php/sargus";

    public static String documento;
    public static String password;
    public static Bitmap logo = null;

    public final static int resCX = 88;
    public final static String pathMainFolfer = Environment.getExternalStorageDirectory() + File.separator + "Sargus";
    public static Usuario usuario = null;

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).build();
    public final static Retrofit retrofit = new Retrofit.Builder().baseUrl(url).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();

    public static boolean isConn;
}
