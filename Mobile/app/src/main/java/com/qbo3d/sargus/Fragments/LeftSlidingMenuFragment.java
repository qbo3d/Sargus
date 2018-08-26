package com.qbo3d.sargus.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qbo3d.sargus.Activities.LoginActivity;
import com.qbo3d.sargus.Activities.MainActivity;
import com.qbo3d.sargus.CustomControls.MenuItemList;
import com.qbo3d.sargus.R;

public class LeftSlidingMenuFragment extends Fragment {

	private RelativeLayout rl_fml_proyecto;
	private MenuItemList mil_ticket;
	private MenuItemList mil_item;
	private MenuItemList mil_captura;
	private MenuItemList mil_logout;
	public static TextView tv_fml_usuario;
	public static TextView tv_fml_proyecto;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		setHasOptionsMenu(true);

		View view = inflater.inflate(R.layout.fragment_main_left, container,false);

		rl_fml_proyecto = view.findViewById(R.id.rl_fml_proyecto);
		mil_ticket = view.findViewById(R.id.mil_ticket);
		mil_item = view.findViewById(R.id.mil_item);
		mil_captura = view.findViewById(R.id.mil_captura);
		mil_logout = view.findViewById(R.id.mil_logout);
		tv_fml_usuario = view.findViewById(R.id.tv_fml_usuario);
		tv_fml_proyecto = view.findViewById(R.id.tv_fml_proyecto);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mil_ticket.setSelected(true);
		}

		rl_fml_proyecto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity.tv_tm_title.setText(getString(R.string.lsm_proyecto));
				switchFragment(getActivity(), new ProyectoFragment());
			}
		});

		mil_ticket.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					mil_ticket.setSelected(true);
					mil_item.setSelected(false);
					mil_captura.setSelected(false);
					mil_logout.setSelected(false);
				}

				MainActivity.tv_tm_title.setText(mil_ticket.getText());
				switchFragment(getActivity(), new TicketFragment());
			}
		});

		mil_item.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					mil_ticket.setSelected(false);
					mil_item.setSelected(true);
					mil_captura.setSelected(false);
					mil_logout.setSelected(false);
				}

				MainActivity.tv_tm_title.setText(mil_item.getText());
				switchFragment(getActivity(), new ItemFragment());
			}
		});

		mil_captura.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					mil_ticket.setSelected(false);
					mil_item.setSelected(false);
					mil_captura.setSelected(true);
					mil_logout.setSelected(false);
				}

				MainActivity.tv_tm_title.setText(mil_captura.getText());
				switchFragment(getActivity(), new CapturaFragment());
			}
		});

		mil_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					mil_ticket.setSelected(false);
					mil_item.setSelected(false);
					mil_captura.setSelected(false);
					mil_logout.setSelected(true);
				}

				MainActivity.tv_tm_title.setText(mil_logout.getText());
				callLogin();
			}
		});

//		if (Vars.usuario != null){
//			tv_fml_operador.setText(Vars.usuario.getObjeto().getId_OperadorLogistico().getNombre());
//			tv_fml_usuario.setText(Vars.usuario.getObjeto().getNombre());
//		}

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
