package com.qbo3d.sargus.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.qbo3d.sargus.Activities.MainActivity;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Serv;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Vars;

public class BarcodeFragment extends Fragment {

    public static EditText et_ab_texto;             //lo que la variable hace
    private ImageButton ib_ab_scan;                 //lo que la variable hace
    private Button bt_ab_buscar;                    //lo que la variable hace
    private static ProgressDialog progressDialog;

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_barcode, container, false);

        et_ab_texto= view.findViewById(R.id.et_ab_texto);
        ib_ab_scan = view.findViewById(R.id.ib_ab_scan);
        bt_ab_buscar = view.findViewById(R.id.bt_ab_buscar);

        progressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        ib_ab_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Vars.barcodeResult = Vars.resBC;
                ((MainActivity)getActivity()).ScanBarcode();
            }
        });

        bt_ab_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_ab_texto.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Debe ingresar un código", Toast.LENGTH_LONG).show();
                } else {
                    if (Util.isConnect(getActivity(), getActivity().getLocalClassName())) {
//                        new MovilizadoDetalleTask(getActivity(), Integer.parseInt(et_ab_texto.getText().toString())).execute((Void) null);
                        Toast.makeText(getActivity(), "Funciona", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return view;
    }

    private static void callMovilizado(Activity activity) {
//        Intent intent = new Intent(activity, MovilizadoActivity.class);
//        activity.startActivityForResult(intent,Vars.resBC);
//        activity.overridePendingTransition(R.anim.transition_left_in, R.anim.transition_left_out);
    }

//    @SuppressLint("StaticFieldLeak")
//    public static class MovilizadoDetalleTask extends AsyncTask<Void, Void, Boolean> {
//
//        private Activity activity;
//        private int Id_Movilizado;
//
//        public MovilizadoDetalleTask(Activity activity, int id_Movilizado) {
//            this.activity = activity;
//            Id_Movilizado = id_Movilizado;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            if (Vars.usuario != null) {
//                Vars.isConn = Serv.getMovilizadoDetalle(Id_Movilizado);
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            progressDialog.dismiss();
//            if (success && Vars.isConn) {
//                Vars.flagScanner = true;
//
//                if (Vars.movilizado != null) {
//                    if (Vars.movilizado.get_id() != null) {
//                        callMovilizado(activity);
//                    } else {
//                        Toast.makeText(activity, "El movilizado #" + et_ab_texto.getText().toString() + " no se ha encontrado.", Toast.LENGTH_SHORT).show();
//                        et_ab_texto.setText("");
//                    }
//                } else {
//                    Toast.makeText(activity, "El movilizado #" + et_ab_texto.getText().toString() + " no se ha encontrado.", Toast.LENGTH_SHORT).show();
//                    et_ab_texto.setText("");
//                }
//            } else {
//                Util.disconnectMessage(activity, activity.getLocalClassName());
//            }
//        }
//    }
}