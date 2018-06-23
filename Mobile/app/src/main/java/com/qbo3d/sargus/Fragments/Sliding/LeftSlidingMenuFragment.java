package com.qbo3d.sargus.Fragments.Sliding;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qbo3d.sargus.Activities.MainActivity;
import com.qbo3d.sargus.CustomControls.MenuItemList;
import com.qbo3d.sargus.Interface.Activities.LoginActivity;
import com.qbo3d.sargus.Interface.Fragments.BarcodeFragment;
import com.qbo3d.sargus.Interface.Fragments.OrdenesServicioFragment;
import com.qbo3d.sargus.Interface.Fragments.RuteoFragment;
import com.qbo3d.sargus.Logic.Util;
import com.qbo3d.sargus.Logic.Vars;

public class LeftSlidingMenuFragment extends Fragment {

	private MenuItemList vi_atencion_soluciones;
	private MenuItemList vi_list;
	private MenuItemList vi_barcode;
	private MenuItemList vi_logout;
	public static TextView tv_fml_operador;
	public static TextView tv_fml_usuario;
	public static ImageView iv_fml_logo;

	private Fragment newContent;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.fragment_main_left, container,false);

		tv_fml_operador = view.findViewById(R.id.tv_fml_operador);
		tv_fml_usuario = view.findViewById(R.id.tv_fml_usuario);
		iv_fml_logo = view.findViewById(R.id.iv_fml_logo);

		newContent = null;

		vi_atencion_soluciones = view.findViewById(R.id.vi_atencion_soluciones);
		vi_atencion_soluciones.setSelected(true);
		vi_atencion_soluciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newContent = new OrdenesServicioFragment();
				vi_atencion_soluciones.setSelected(true);
				vi_list.setSelected(false);
				vi_barcode.setSelected(false);
				vi_logout.setSelected(false);

				MainActivity.ivTitleMenu.setVisibility(View.GONE);
				MainActivity.ivTitleName.setText(vi_atencion_soluciones.getText());

				if (newContent != null)
					switchFragment(getActivity(), newContent);
			}
		});

		vi_list = view.findViewById(R.id.vi_list);
		vi_list.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newContent = new RuteoFragment();
				vi_atencion_soluciones.setSelected(false);
				vi_list.setSelected(true);
				vi_barcode.setSelected(false);
				vi_logout.setSelected(false);
				MainActivity.ivTitleMenu.setVisibility(View.VISIBLE);

				MainActivity.ivTitleName.setText(vi_list.getText());

				if (newContent != null)
					switchFragment(getActivity(), newContent);
			}
		});

		vi_barcode = view.findViewById(R.id.vi_barcode);
		vi_barcode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newContent = new BarcodeFragment();
				vi_atencion_soluciones.setSelected(false);
				vi_list.setSelected(false);
				vi_barcode.setSelected(true);
				vi_logout.setSelected(false);
				MainActivity.ivTitleMenu.setVisibility(View.GONE);

				MainActivity.ivTitleName.setText(vi_barcode.getText());

				if (newContent != null)
					switchFragment(getActivity(), newContent);
			}
		});

		vi_logout = view.findViewById(R.id.vi_logout);
		vi_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				vi_atencion_soluciones.setSelected(false);
				vi_list.setSelected(false);
				vi_barcode.setSelected(false);
				vi_logout.setSelected(true);

				Util.eliminarLogo();

				MainActivity.ivTitleMenu.setVisibility(View.GONE);
				MainActivity.ivTitleName.setText(vi_logout.getText());

				callLogin();

				if (newContent != null)
					switchFragment(getActivity(), newContent);
			}
		});

		if (Vars.usuario != null){
			tv_fml_operador.setText(Vars.usuario.getObjeto().getId_OperadorLogistico().getNombre());
			tv_fml_usuario.setText(Vars.usuario.getObjeto().getNombre());
		}

		return view;
    }
	
	public static void switchFragment(Activity activity, Fragment fragment) {
		if (activity == null)
			return;

		if (activity instanceof MainActivity) {
			MainActivity fca = (MainActivity) activity;
			fca.switchContent(fragment);
		}

		MainActivity ra = (MainActivity) activity;
		ra.switchContent(fragment);
	}

	private void callLogin() {
		SharedPreferences userDetails = PreferenceManager.getDefaultSharedPreferences(getContext());
		SharedPreferences.Editor edit = userDetails.edit();
		edit.clear();
		edit.apply();
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		startActivity(intent);
		getActivity().finish();
	}

}
