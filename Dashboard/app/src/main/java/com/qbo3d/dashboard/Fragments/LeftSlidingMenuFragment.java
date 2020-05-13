package com.qbo3d.dashboard.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qbo3d.dashboard.Activities.LoginActivity;
import com.qbo3d.dashboard.Activities.MainActivity;
import com.qbo3d.dashboard.Chart.SineCosineFragment;
import com.qbo3d.dashboard.CustomControls.MenuItemList;
import com.qbo3d.dashboard.R;
import com.qbo3d.dashboard.Vars;

public class LeftSlidingMenuFragment extends Fragment {

	private RelativeLayout rl_fml_proyecto;
	private MenuItemList mil_inicio;
	private MenuItemList mil_se1;
	private MenuItemList mil_se2;
	private MenuItemList mil_se1_lista;
	private MenuItemList mil_se2_lista;
	private MenuItemList mil_logout;
	public static TextView tv_fml_usuario;
	public static TextView tv_fml_proyecto;

	private static Activity act;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    }

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		act = getActivity();

		View view = inflater.inflate(R.layout.fragment_main_left, container,false);

		rl_fml_proyecto = view.findViewById(R.id.rl_fml_proyecto);
		mil_inicio = view.findViewById(R.id.mil_inicio);
		mil_se1 = view.findViewById(R.id.mil_se1);
		mil_se2 = view.findViewById(R.id.mil_se2);
		mil_se1_lista = view.findViewById(R.id.mil_se1_lista);
		mil_se2_lista = view.findViewById(R.id.mil_se2_lista);
		mil_logout = view.findViewById(R.id.mil_logout);
		tv_fml_usuario = view.findViewById(R.id.tv_fml_usuario);
		tv_fml_proyecto = view.findViewById(R.id.tv_fml_proyecto);

        mil_inicio.setSelected(true);

		rl_fml_proyecto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				MainActivity.tv_tm_title.setText(getString(R.string.lsm_proyecto));
				switchFragment(getActivity(), new ProyectoFragment());
			}
		});

		mil_inicio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(true);
				mil_se1.setSelected(false);
				mil_se2.setSelected(false);
				mil_se1_lista.setSelected(false);
				mil_se2_lista.setSelected(false);
				mil_logout.setSelected(false);

				MainActivity.tv_tm_title.setText(mil_inicio.getText());
				switchFragment(getActivity(), new SineCosineFragment());
				Vars.currentLocation = "";
			}
		});

		mil_se1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(false);
				mil_se1.setSelected(true);
				mil_se2.setSelected(false);
				mil_se1_lista.setSelected(false);
				mil_se2_lista.setSelected(false);
				mil_logout.setSelected(false);

				MainActivity.tv_tm_title.setText(mil_se1.getText());
				switchFragment(getActivity(), new SineCosineFragment());
				Vars.currentLocation = "SE1";

			}
		});

		mil_se2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(false);
				mil_se1.setSelected(false);
				mil_se2.setSelected(true);
				mil_se1_lista.setSelected(false);
				mil_se2_lista.setSelected(false);
				mil_logout.setSelected(false);

				MainActivity.tv_tm_title.setText(mil_se2.getText());
				switchFragment(getActivity(), new SineCosineFragment());
				Vars.currentLocation = "SE2";

			}
		});

		mil_se1_lista.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(false);
				mil_se1.setSelected(false);
				mil_se2.setSelected(false);
				mil_se1_lista.setSelected(true);
				mil_se2_lista.setSelected(false);
				mil_logout.setSelected(false);

				MainActivity.tv_tm_title.setText(mil_se1_lista.getText());
				switchFragment(getActivity(), new ListViewBarChartFragment());
				Vars.currentLocation = "SE1";

			}
		});

		mil_se2_lista.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(false);
				mil_se1.setSelected(false);
				mil_se2.setSelected(false);
				mil_se1_lista.setSelected(false);
				mil_se2_lista.setSelected(true);
				mil_logout.setSelected(false);

				MainActivity.tv_tm_title.setText(mil_se2_lista.getText());
				switchFragment(getActivity(), new ListViewBarChartFragment());
				Vars.currentLocation = "SE2";

			}
		});

		mil_logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mil_inicio.setSelected(false);
				mil_se1.setSelected(false);
				mil_logout.setSelected(true);

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
