package com.qbo3d.dashboard.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;

import com.google.android.material.snackbar.Snackbar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.qbo3d.dashboard.Chart.SineCosineFragment;
import com.qbo3d.dashboard.DialogFragment.DialogFragmentVal;
import com.qbo3d.dashboard.Fragments.LeftSlidingMenuFragment;
import com.qbo3d.dashboard.Models.Usuario;
import com.qbo3d.dashboard.R;
import com.qbo3d.dashboard.Util;
import com.qbo3d.dashboard.Serv;
import com.qbo3d.dashboard.Vars;

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener
//        , DialogFragmentVal.Respuesta
{

    public static SlidingMenu leftRightSlidingMenu;
    public static ImageButton ib_tm_hamburguesa;
    public static TextView tv_tm_title;
    public static ImageButton ib_tm_menu;
    private Fragment mContent;

    public static View mFragmentFormView;

    public static ProgressDialog progressDialog;
    private IntentIntegrator integrator;

    public static Button bt_tm_on;
    public static Button bt_tm_off;
    public static ProgressBar pb_tm_indicador;

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Vars.currentProject = "Varsovia";

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        checkPermission();

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        initLeftRightSlidingMenu();
        setContentView(R.layout.activity_main);

        mActivity = this;

        Util.createFolders(this);

        initView();

        tv_tm_title.setText(getResources().getString(R.string.lsm_inicio));

        String documento;
        String password;

        if (TextUtils.isEmpty(Vars.documento) && TextUtils.isEmpty(Vars.password)) {
            documento = Util.getStorageString(getBaseContext(), "documento");
            password = Util.getStorageString(getBaseContext(), "password");
        } else {
            documento = Vars.documento;
            password = Vars.password;
        }

        if (TextUtils.isEmpty(documento) || TextUtils.isEmpty(password)) {
            callLogin();
        } else {
            register(documento, password);
        }

        bt_tm_on = (Button) findViewById(R.id.bt_tm_on);
        bt_tm_off = (Button) findViewById(R.id.bt_tm_off);
        pb_tm_indicador = (ProgressBar) findViewById(R.id.pb_tm_indicador);
    }

    private void callLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        mFragmentFormView = findViewById(R.id.content_frame);
        ib_tm_hamburguesa = (ImageButton) this.findViewById(R.id.ib_tm_hamburguesa);
        tv_tm_title = (TextView) this.findViewById(R.id.tv_tm_title);
        ib_tm_menu = (ImageButton) this.findViewById(R.id.ib_tm_menu);
        ib_tm_hamburguesa.setOnClickListener(this);
        ib_tm_menu.setOnClickListener(this);
    }

    private void initLeftRightSlidingMenu() {
        mContent = new SineCosineFragment();
        Vars.currentLocation = "";
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
        setBehindContentView(R.layout.layout_main_left);
        FragmentTransaction leftFragementTransaction = getSupportFragmentManager().beginTransaction();
        Fragment leftFrag = new LeftSlidingMenuFragment();
        leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
        leftFragementTransaction.commit();

        // customize the SlidingMenu
        leftRightSlidingMenu = getSlidingMenu();
        leftRightSlidingMenu.setMode(SlidingMenu.LEFT);
        leftRightSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        leftRightSlidingMenu.setFadeDegree(0.35f);
        leftRightSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow);
        leftRightSlidingMenu.setFadeEnabled(true);
        leftRightSlidingMenu.setBehindScrollScale(0.333f);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_tm_hamburguesa:
                leftRightSlidingMenu.showMenu();
                break;
            default:
                break;
        }
    }

    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
        getSlidingMenu().showContent();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager manager = getFragmentManager();
            android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
            if (frag != null) {
                manager.beginTransaction().remove(frag).commit();
            }
            DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content), "Ok", "");
            editNameDialog.show(manager, "fragment_edit_name");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void onFinishVal(int result, boolean op) {
//
//        if (op) {
//
//        }
//    }

    @SuppressLint("StaticFieldLeak")
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private Activity mActivity;
        private final String mDocumento;
        private final String mPassword;

        LoginTask(Activity activity, String documento, String password) {
            mActivity = activity;
            mDocumento = documento;
            mPassword = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String objetoLogin = Util.getStorageString(getBaseContext(), "objeto_login");

            if (objetoLogin == null || objetoLogin.equals("")) {
                Vars.isConn = Serv.getLogin(mActivity, mDocumento, mPassword);
            } else {
                Vars.isConn = true;
                Gson gson = new Gson();
                Vars.usuario = gson.fromJson(objetoLogin, Usuario.class);
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            progressDialog.dismiss();
            if (success && Vars.isConn) {
                if (Vars.usuario != null) {
                    if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
//                        new AllTicketTask().execute();
                    }
                    LeftSlidingMenuFragment.tv_fml_usuario.setText(Vars.usuario.getNombre());
                    LeftSlidingMenuFragment.tv_fml_proyecto.setText(Vars.usuario.getEntidad());
                }
            } else {
                Util.disconnectMessage(mActivity, mActivity.getLocalClassName());
            }
        }
    }

    private void register(final String documento, final String password) {


        if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
            new LoginTask(mActivity, documento, password).execute((Void) null);
        }
        //send this registrationId to your server


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//		if (resultCode == Vars.resOS){
//			if (Util.isConnect(activity)) {
//				new OrdenesServicioFragment.OrdenServicioTask(this).execute((Void) null);
//			}
//		}
//
//		if (resultCode == Vars.resMO){
//			if (Util.isConnect(this)) {
//				new RuteoFragment.ManifiestoDetalleTask(Vars.usuario.getObjeto().get_id(), this).execute((Void) null);
//			}
//		}
//
//		if (resultCode == Vars.resCI){
//			if (Util.isConnect(this)) {
//
//				if (resultCode == Vars.resCS){	new OrdenServicioTask().execute((Void) null);
//				}
//			}
//		}
//
//		if (requestCode == IntentIntegrator.REQUEST_CODE) {
//			if (data != null) {
//					String contents = data.getStringExtra("SCAN_RESULT");
//
//					Toast.makeText(this, "Código escaneado: " + contents, Toast.LENGTH_LONG).show();
//
//				try {
//					Vars.idMovilizado = Integer.parseInt(contents);
//				}catch(NumberFormatException nfe){
//					Vars.idMovilizado = 0;
//				}
//
//				BarcodeFragment.et_ab_texto.setText(String.valueOf(Vars.idMovilizado));
//				} else {
//					Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
//				}
//		}

//        if (requestCode == IntentIntegrator.REQUEST_CODE) {
//            if (data != null) {
//                String contents = data.getStringExtra("SCAN_RESULT").replace("\n", "");
//
////				if (Vars.resBC){
//                Toast.makeText(this, "Código escaneado: " + contents, Toast.LENGTH_LONG).show();
//
//                BarcodeFragment.et_ab_texto.setText(contents);
//            } else {
//                Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case Vars.PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean locationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (locationPermission && readExternalFile) {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to upload profile photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(
                                                    new String[]{
                                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                                            Manifest.permission.ACCESS_FINE_LOCATION
                                                    },
                                                    Vars.PERMISSIONS_MULTIPLE_REQUEST);
                                        }
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Faltan permisos por habilitar",
                        Snackbar.LENGTH_INDEFINITE).setAction("Habilitar",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(
                                            new String[]{
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                            }, Vars.PERMISSIONS_MULTIPLE_REQUEST);
                                }
                            }
                        }).show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            }, Vars.PERMISSIONS_MULTIPLE_REQUEST);
                }
            }
        } else {
            // write your logic code if permission already granted
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
