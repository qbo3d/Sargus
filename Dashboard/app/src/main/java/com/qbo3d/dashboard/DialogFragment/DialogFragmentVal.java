package com.qbo3d.dashboard.DialogFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qbo3d.dashboard.R;

/**
 * Created by Way Solutions on 7/2/2018.
 */

@SuppressLint("ValidFragment")
public class DialogFragmentVal extends android.app.DialogFragment {

    private TextView tv_fcl_titulo;
    private TextView tv_fcl_contenido;
    private Button btn_fcl_ok;
    private Button btn_fcl_cancel;

    private int result;
    private String titulo;
    private String contenido;
    private String okStr;
    private String cancelStr;

//    public interface Respuesta {
//        void onFinishVal(int result, boolean op);
//    }

    public DialogFragmentVal(int result, String titulo, String contenido) {
        this.result = result;
        this.titulo = titulo;
        this.contenido = contenido;
        this.okStr = "Ok";
        this.cancelStr = "Cancelar";
    }

    public DialogFragmentVal(int result, String titulo, String contenido, String okStr, String cancelStr) {
        this.result = result;
        this.titulo = titulo;
        this.contenido = contenido;
        this.okStr = okStr;
        this.cancelStr = cancelStr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_val, container);

        tv_fcl_titulo = view.findViewById(R.id.tv_fcl_titulo);
        tv_fcl_contenido = view.findViewById(R.id.tv_fcl_contenido);
        btn_fcl_ok = view.findViewById(R.id.btn_fcl_ok);
        btn_fcl_cancel = view.findViewById(R.id.btn_fcl_cancel);

        if (!okStr.equals("")){
            btn_fcl_ok.setText(okStr);
        }

        if (cancelStr.equals("")){
            btn_fcl_cancel.setText(cancelStr);
            btn_fcl_cancel.setVisibility(View.GONE);
        }

        tv_fcl_titulo.setText(titulo);
        tv_fcl_contenido.setText(contenido);

        btn_fcl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DialogFragmentVal.Respuesta activity = (DialogFragmentVal.Respuesta) getActivity();
//                activity.onFinishVal(result, true);
                dismiss();
            }
        });

        btn_fcl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DialogFragmentVal.Respuesta activity = (DialogFragmentVal.Respuesta) getActivity();
//                activity.onFinishVal(result, false);
                dismiss();
            }
        });

        return view;
    }
}