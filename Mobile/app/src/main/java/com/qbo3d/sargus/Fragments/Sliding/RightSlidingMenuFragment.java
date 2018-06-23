package com.qbo3d.sargus.Fragments.Sliding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.waysolutions.waylogistic.Interface.Activities.OrdenServicioActivity;
import com.waysolutions.waylogistic.Interface.DialogFragment.DialogFragmentVal;
import com.waysolutions.waylogistic.Logic.Objects.OrdenServicio.OrdenServicio;
import com.waysolutions.waylogistic.Logic.Util;
import com.waysolutions.waylogistic.Logic.Vars;
import com.waysolutions.waylogistic.R;

import java.util.LinkedList;

public class RightSlidingMenuFragment extends Fragment implements
		DialogFragmentVal.Respuesta{

	private ProgressDialog progressDialog;

	@SuppressLint("StaticFieldLeak")
	public static ListView mrf_listview;
	private TextView tv_notificacion;
//	private SwipeRefreshLayout mRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_main_rigth, container, false);
		tv_notificacion = getActivity().findViewById(R.id.tv_notificacion);
		mrf_listview = view.findViewById(R.id.mrf_listview);
//		mRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_N);

		if (Vars.ordenes != null) {
			Util.cargar(getActivity(), mrf_listview, R.layout.itemlist_sinleer, Util.actNoLeidos(tv_notificacion), true);
		}

		progressDialog = new ProgressDialog(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.pd_cargando));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		mrf_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int pos = 0;
				callOrdenServicio();

				for(OrdenServicio os : Vars.ordenes){
					OrdenServicio osAux = (OrdenServicio) mrf_listview.getAdapter().getItem(position);
//					if (os.getId().equals(mrf_listview.getAdapter().getItem(position).toString().substring(Vars.titulo_orden_servicio.length()))){
					if (os.getId().equals(osAux.getId())){
						Vars.posOs = pos;
					}
					pos ++;
				}

				if (Util.isConnect(getActivity())) {
					if (!Vars.ordenes[Vars.posOs].isLeido()) {
						new ActualizarOrdenTask(Vars.ordenes[Vars.posOs].get_id(), getActivity(), mrf_listview, R.layout.itemlist_sinleer, Util.actNoLeidos(tv_notificacion), true).execute((Void) null);
					}
				}
			}
		});

		return view;
	}

	private void callOrdenServicio() {
		Intent intent = new Intent(getActivity(), OrdenServicioActivity.class);
		startActivityForResult(intent, Vars.resOS);
		getActivity().overridePendingTransition(R.anim.transition_left_in, R.anim.transition_left_out);
	}

	public class ActualizarOrdenTask extends AsyncTask<Void, Void, Boolean> {

		private String orden;
		private Activity act;
		private ListView listview;
		private int resource;
		private LinkedList<OrdenServicio> data;
		private boolean no_leido;

		public ActualizarOrdenTask(String orden, Activity act, ListView listview,
                                   int resource, LinkedList<OrdenServicio> data, boolean no_leido) {
			this.orden = orden;
			this.act = act;
			this.listview = listview;
			this.resource = resource;
			this.data = data;
			this.no_leido = no_leido;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

//        try {
				Util.getActualizarOrden(orden);

				for(OrdenServicio os : Vars.ordenes){
					if(os.get_id().equals(orden)){
						os.setLeido(true);
					}
				}

//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            Log.e("WSError", e.getMessage());
//            return false;
//        }
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				if (Vars.ordenes != null) {
					Util.cargar(act, listview, resource, data, no_leido);
				}
			}
		}

		@Override
		protected void onCancelled() {
		}
	}

	@Override
	public void onFinishVal(int result, boolean op) {

	}
}
