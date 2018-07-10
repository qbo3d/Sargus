package com.qbo3d.sargus.Fragments.Sliding;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qbo3d.sargus.R;

public class RightSlidingMenuFragment extends Fragment {

	private ProgressDialog progressDialog;

	@SuppressLint("StaticFieldLeak")

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_rigth, container, false);

		progressDialog = new ProgressDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.pd_cargando));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		return view;
	}
}
