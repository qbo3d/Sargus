package com.qbo3d.sargus.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Vars;

public class ItemFragment extends Fragment {

    public static Spinner sp_fi_groups;
    private static ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_item, container, false);

        sp_fi_groups = view.findViewById(R.id.sp_fi_groups);

        progressDialog = new ProgressDialog(getActivity(), R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        if (Vars.grupos != null) {
            Util.cargarGroups(getActivity(), sp_fi_groups, Util.groupListToData());
        }

        return view;
    }
}
