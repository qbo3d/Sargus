package com.qbo3d.sargus.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.waysolutions.waylogistic.Interface.DialogFragment.DialogFragmentEstado;
import com.waysolutions.waylogistic.Interface.DialogFragment.DialogFragmentMenu;
import com.waysolutions.waylogistic.Interface.DialogFragment.DialogFragmentVal;
import com.waysolutions.waylogistic.Interface.DialogFragment.DialogFragmentWS;
import com.waysolutions.waylogistic.Interface.Fragments.BarcodeFragment;
import com.waysolutions.waylogistic.Interface.Fragments.OrdenesServicioFragment;
import com.waysolutions.waylogistic.Interface.Fragments.RuteoFragment;
import com.waysolutions.waylogistic.Interface.Fragments.Sliding.LeftSlidingMenuFragment;
import com.waysolutions.waylogistic.Interface.Fragments.Sliding.RightSlidingMenuFragment;
import com.waysolutions.waylogistic.Logic.CaptureActivityPortrait;
import com.waysolutions.waylogistic.Logic.Enumeradores.ProcesosDelSistema;
import com.waysolutions.waylogistic.Logic.Objects.ActualizarRutaMovilizado.ActualizarRutaMovilizado;
import com.waysolutions.waylogistic.Logic.Objects.Concepto.ConceptoProceso;
import com.waysolutions.waylogistic.Logic.Objects.Concepto.Conceptos;
import com.waysolutions.waylogistic.Logic.Objects.Formato.Formato;
import com.waysolutions.waylogistic.Logic.Util;
import com.waysolutions.waylogistic.Logic.Utilities.GCMClientManager;
import com.waysolutions.waylogistic.Logic.Utilities.ImageHelper;
import com.waysolutions.waylogistic.Logic.Vars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener,
		DialogFragmentWS.Respuesta,
		DialogFragmentVal.Respuesta,
		DialogFragmentMenu.Respuesta,
		DialogFragmentEstado.Respuesta,
		LoaderManager.LoaderCallbacks<Object> {

	public static SlidingMenu leftRightSlidingMenu;
	public static ImageButton ivTitleBtnLeft;
	public static TextView ivTitleName;
	public static ImageButton badgeBtnRight;
	private Fragment mContent;
	public static ImageButton ivTitleMenu;

	public static View mFragmentFormView;

	private SharedPreferences userDetails;

	private TextView tv_notificacion;

	public static ProgressDialog progressDialog;
	private IntentIntegrator integrator;

	private boolean push = false;

	private Activity activity;

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		Crear forlder donde se almacena el logo del operador logístico
		Util.createFolders(this);

		progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.pd_cargando));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		initLeftRightSlidingMenu();
		setContentView(R.layout.activity_main);

		activity = this;

		//TODO Permisos
		populateAutoComplete();

		initView();

		ivTitleName.setText(getString(R.string.listview));

		userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String usuario = userDetails.getString("usuario", "");
		String password = userDetails.getString("password", "");

//		if (Vars.usuario == null){
			if (TextUtils.isEmpty(usuario) && TextUtils.isEmpty(password)){
				callLogin();
			} else {
				register(usuario, password);
			}
//		}

		if (Vars.formato != null) {
			if (Vars.pn_id_orden == null || Vars.pn_id_orden.equals("")) {
				push = false;
				Log.d("Main", "Push false");
			} else {
				push = true;
				if (Vars.pn_tipo.equals("orden")) {
					if (Util.isConnect(this)) {
						new OrdenServicioTask().execute((Void) null);
					}
				} else {
					Fragment newContent = new RuteoFragment();
					ivTitleMenu.setVisibility(View.VISIBLE);

					ivTitleName.setText("Lista de ruteo");

					if (newContent != null)
						LeftSlidingMenuFragment.switchFragment(this, newContent);
				}
				Log.d("Main", "Push true");
			}
		}
	}

	private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
	private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;
	private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
	private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 4;
	private static final int REQUEST_BLUETOOTH = 5;
	private static final int REQUEST_CAMERA = 6;

	//TODO Permisos
	private void populateAutoComplete() {
        if (!mayRequestLocation()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

	//TODO Permisos
	private boolean mayRequestLocation() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			return true;
		}
		if (checkSelfPermission(BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
			return true;
		}
		if (shouldShowRequestPermissionRationale(BLUETOOTH)) {
			// To use the Snackbar from the design support library, ensure that the activity extends
			// AppCompatActivity and uses the Theme.AppCompat theme.
		} else {
			requestPermissions(new String[]{BLUETOOTH}, REQUEST_BLUETOOTH);
		}
		return false;
	}

	private void permisos(){
		if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, 1);
		}else if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 2);
		}else if (ContextCompat.checkSelfPermission(this, BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH}, 3);
		}else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 4);
		}
	}

	private void callOrdenServicio() {
		Intent intent = new Intent(this, OrdenServicioActivity.class);
		startActivity(intent);
	}

	private void callLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		mFragmentFormView = findViewById(R.id.content_frame);
		ivTitleBtnLeft = (ImageButton) this.findViewById(R.id.ivTitleBtnLeft);
		ivTitleName = (TextView) this.findViewById(R.id.ivTitleName);
		ivTitleBtnLeft.setOnClickListener(this);
		badgeBtnRight = (ImageButton)this.findViewById(R.id.badgeBtnRight);
		tv_notificacion = (TextView) this.findViewById(R.id.tv_notificacion);
		ivTitleMenu = (ImageButton) this.findViewById(R.id.ivTitleMenu);
		badgeBtnRight.setOnClickListener(this);
	}

	private void initLeftRightSlidingMenu() {
		mContent = new OrdenesServicioFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		setBehindContentView(R.layout.layout_main_left);
		FragmentTransaction leftFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment leftFrag = new LeftSlidingMenuFragment();
		leftFragementTransaction.replace(R.id.main_left_fragment, leftFrag);
		leftFragementTransaction.commit();

		// customize the SlidingMenu
		leftRightSlidingMenu = getSlidingMenu();
		leftRightSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		leftRightSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		leftRightSlidingMenu.setFadeDegree(0.35f);
		leftRightSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		leftRightSlidingMenu.setShadowDrawable(R.drawable.shadow);
		leftRightSlidingMenu.setFadeEnabled(true);
		leftRightSlidingMenu.setBehindScrollScale(0.333f);

		leftRightSlidingMenu.setSecondaryMenu(R.layout.layout_main_right);
		FragmentTransaction rightFragementTransaction = getSupportFragmentManager().beginTransaction();
		Fragment rightFrag = new RightSlidingMenuFragment();
		leftFragementTransaction.replace(R.id.main_right_fragment, rightFrag);
		rightFragementTransaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ivTitleBtnLeft:
				leftRightSlidingMenu.showMenu();
				break;
			case R.id.badgeBtnRight:
				leftRightSlidingMenu.showSecondaryMenu(true);
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
			DialogFragmentWS editNameDialog = new DialogFragmentWS(true, getResources().getString(R.string.main_salir), getResources().getString(R.string.main_seguro), "Ok", getResources().getString(R.string.main_salir), true, true, false);
			editNameDialog.show(manager, "fragment_edit_name");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFinishCheckDialog(int detalle, int proceso, Conceptos conceptos) {
		if (proceso == -1) {
			System.exit(0);
		}
	}

	@Override
	public void onFinishCheckDialog(int result) {

	}

	@Override
	public void onFinishVal(int result, boolean op) {

		if (op) {
			if (result == Vars.resRFtrue) {
				Vars.imagen = false;
				ivTitleMenu.setVisibility(View.GONE);
				RuteoFragment.mDragListView.setDragEnabled(false);
				RuteoFragment.fab_rto_cerrar.setBackgroundTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.verde));

				//TODO consumir servicio de juandavid de cambio de estado de ruteo
					new ActualizarRutaMovilizadoTask(
							new ActualizarRutaMovilizado(
									Util.getOrdenRuta(RuteoFragment.mDragListView.getAdapter().getItemList()),
									true
							)
					).execute();
//					Util.postActualizarRutaMovilizado(
//							new ActualizarRutaMovilizado(
//									Util.strArrToIntArr(Vars.setDragEnabled.split(" - ")),
//									true
//							)
//
//					);

//				for (OrdenServicio ordenServicio : Vars.ordenes) {
//					for (Detalle de : ordenServicio.getDetalle()) {
//						for (Integer id : Vars.setDragEnabled) {
//							if (de.getId() == id) {
//								de.setEn_ruta(true);
//								break;
//							}
//						}
//					}
//				}

				RuteoFragment.fab_rto_cerrar.setBackgroundTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.verde));
				MainActivity.ivTitleMenu.setVisibility(View.GONE);
				Vars.movilizados[0].setEn_ruta(true);
			} else if (result == Vars.resRFfalse) {

					new ActualizarRutaMovilizadoTask(
							new ActualizarRutaMovilizado(
									Util.getOrdenRuta(RuteoFragment.mDragListView.getAdapter().getItemList()),
									false
							)
					).execute();
//					Util.postActualizarRutaMovilizado(
//							new ActualizarRutaMovilizado(
//									Util.strArrToIntArr(Vars.setDragEnabled.split(" - ")),
//									false
//							)
//
//					);

//				Vars.setDragEnabled = new ArrayList<>();

				RuteoFragment.fab_rto_cerrar.setBackgroundTintList(ContextCompat.getColorStateList(getBaseContext(), R.color.rojo));
				MainActivity.ivTitleMenu.setVisibility(View.VISIBLE);

//                    fab_rto_cerrar.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_send_blanco));
				RuteoFragment.fab_rto_cerrar.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.rojo));
				Vars.imagen = true;
//                    fab_rto_cerrar.setText(getString(R.string.rto_iniciar_ruta) + " con " + String.valueOf(items) + " Movilizados");
				ivTitleMenu.setVisibility(View.VISIBLE);
				RuteoFragment.mDragListView.setDragEnabled(true);
				Vars.movilizados[0].setEn_ruta(false);
			}
		}
	}

	@SuppressLint("StaticFieldLeak")
	public static class ActualizarRutaMovilizadoTask extends AsyncTask<Void, Void, Boolean> {

		private ActualizarRutaMovilizado actualizarRutaMovilizado;

		public ActualizarRutaMovilizadoTask(ActualizarRutaMovilizado actualizarRutaMovilizado) {
			this.actualizarRutaMovilizado = actualizarRutaMovilizado;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Util.postActualizarRutaMovilizado(actualizarRutaMovilizado);
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {

			}
		}
	}

	@Override
	public void onFinishDFMenu(int detalle) {
		Vars.ordenMov = detalle;
		if (Util.isConnect(this)) {
			new RuteoFragment.ManifiestoDetalleTask(Vars.usuario.getObjeto().get_id(), this).execute((Void) null);
		}
		if (RuteoFragment.mDragListView.getAdapter().getItemList().size() == 0){
			ivTitleMenu.setVisibility(View.GONE);
			RuteoFragment.fab_rto_cerrar.setVisibility(View.GONE);
		} else {
			ivTitleMenu.setVisibility(View.VISIBLE);
			RuteoFragment.fab_rto_cerrar.setVisibility(View.VISIBLE);
		}
	}


	//Metodos del implement LoaderManager
	@Override
	public Loader<Object> onCreateLoader(int i, Bundle bundle) {
		return null;
	}

	@Override
	public void onLoadFinished(Loader<Object> loader, Object o) {

	}

	@Override
	public void onLoaderReset(Loader<Object> loader) {

	}

	@Override
	public void onFinishEstado(int estado, int item) {
		Vars.posMovByLR = item;
		callMovilizado();
	}

	private void callMovilizado() {
		Vars.flagScanner = false;
		Vars.images = new ArrayList<>();
		Vars.fotos = new ArrayList<>();
		Vars.conceptos = null;
		Intent intent = new Intent(this, MovilizadoActivity.class);
		startActivityForResult(intent,Vars.resMO);
		overridePendingTransition(R.anim.transition_left_in, R.anim.transition_left_out);
	}

	@SuppressLint("StaticFieldLeak")
	public class ObjetoLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mEmail;
		private final String mPassword;
		private final String mId;

		ObjetoLoginTask(String email, String password, String Id) {
			mEmail = email;
			mPassword = password;
			mId = Id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Vars.inMain = true;
			progressDialog.show();
			Log.d("Progress", "main Show1");
//			leftRightSlidingMenu.setSlidingEnabled(false);
//			ivTitleBtnLeft.setVisibility(View.GONE);
//			badgeBtnRight.setVisibility(View.GONE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Util.getObjetoLogin(mPassword, mEmail, mId);
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (success) {
				progressDialog.dismiss();
				//TODO aqui una que ptra vez hay un error al inicial
				if (Vars.usuario != null){
					if (!Vars.usuario.getEstado()){
						callLogin();
					} else {
						if (Util.isConnect(activity)) {
							new OrdenServicioTask().execute((Void) null);
							if (Vars.usuario != null){
								LeftSlidingMenuFragment.tv_fml_operador.setText(Vars.usuario.getObjeto().getId_OperadorLogistico().getNombre());
								LeftSlidingMenuFragment.tv_fml_usuario.setText(Vars.usuario.getObjeto().getNombre());
							}
							if (!Vars.test) {
								new DownloadImagesTask().execute();
							}
						}
//							} else {
//								editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
//								editNameDialog.setCancelable(false);
//								editNameDialog.show(manager, "fragment_edit_name");
//							}
//						} else {
//							editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_net_error), getString(R.string.login_net_error_content), "Ok", "");
//							editNameDialog.setCancelable(false);
//							editNameDialog.show(manager, "fragment_edit_name");
//						}
					}
				} else {
					if (Util.isConnect(activity)) {
						new ObjetoLoginTask(mEmail, mPassword, mId).execute((Void) null);
					}
//						} else {
//							editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
//							editNameDialog.setCancelable(false);
//							editNameDialog.show(manager, "fragment_edit_name");
//						}
//					} else {
//						editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_net_error), getString(R.string.login_net_error_content), "Ok", "");
//						editNameDialog.setCancelable(false);
//						editNameDialog.show(manager, "fragment_edit_name");
//					}
				}
			}
		}

		@Override
		protected void onCancelled() {
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class DownloadImagesTask extends AsyncTask<Void, Void, Bitmap> {

		@SuppressLint("ResourceType")
		@Override
		protected Bitmap doInBackground(Void... params) {

			try {
				if (new File(Vars.pathMainFolfer  + File.separator + Vars.usuario.getObjeto().getNombreLogo()).exists()) {
					FileInputStream streamIn = new FileInputStream(Vars.pathMainFolfer  + File.separator + Vars.usuario.getObjeto().getNombreLogo());
					Vars.logo = BitmapFactory.decodeStream(streamIn);
				} else {

					Vars.logo = download_Image(Vars.usuario.getServidor() + Vars.usuario.getObjeto().getUrl_logo());

					if (Vars.logo == null) {
						Vars.logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
					}

//			Almacenamiento de bitmap

					OutputStream fOutputStream;
					File file = new File(Vars.pathMainFolfer, Vars.usuario.getObjeto().getNombreLogo());
					fOutputStream = new FileOutputStream(file);

					Vars.logo.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);

					fOutputStream.flush();
					fOutputStream.close();

					MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				Toast.makeText(activity, "Save Failed", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				e.printStackTrace();
				Toast.makeText(activity, "Save Failed", Toast.LENGTH_SHORT).show();
			}

			return Vars.logo;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				LeftSlidingMenuFragment.iv_fml_logo.setImageBitmap(result);
				Bitmap roundedCornersImage = ImageHelper.getRoundedCornerBitmap(result, 104);
				LeftSlidingMenuFragment.iv_fml_logo.setImageBitmap(roundedCornersImage);
			}
		}

		private Bitmap download_Image(String url) {
			Bitmap bitmap = null;
			InputStream stream = null;
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 1;

			try {
				stream = getHttpConnection(url);
				if (stream != null){
					bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
					stream.close();
				}
			}
			catch (IOException e1) {
				e1.printStackTrace();
				System.out.println("downloadImage"+ e1.toString());
			}
			return bitmap;
		}

		public InputStream getHttpConnection(String urlString)  throws IOException {

			InputStream stream = null;
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();

			try {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setRequestMethod("GET");
				httpConnection.connect();

				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					stream = httpConnection.getInputStream();
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
				System.out.println("downloadImage" + ex.toString());
			}
			return stream;
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class OrdenServicioTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
			Log.d("Progress", "main Show2");
//			leftRightSlidingMenu.setSlidingEnabled(false);
//			ivTitleBtnLeft.setVisibility(View.GONE);
//			badgeBtnRight.setVisibility(View.GONE);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			if (Vars.usuario != null){
				Util.getOrdenServicioList(Vars.usuario.getObjeto().get_id());
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				progressDialog.dismiss();
				if(push){
					Util.ubicarPosOSByOrden(Vars.pn_id_orden);
					callOrdenServicio();
//                new ManifiestoTask().execute((Void) null);
				} else {
					progressDialog.dismiss();
					if (Vars.ordenes != null) {
//						Util.cargar(MainActivity.this, RightSlidingMenuFragment.mrf_listview, R.layout.itemlist_sinleer, Util.actNoLeidos(tv_notificacion), true);
						if (OrdenesServicioFragment.fos_listView != null) {
							Util.cargar(MainActivity.this, OrdenesServicioFragment.fos_listView, R.layout.itemlist_sinleer, Util.actActivo(), false);
							Util.cargar(MainActivity.this, RightSlidingMenuFragment.mrf_listview, R.layout.itemlist_sinleer, Util.actNoLeidos(tv_notificacion), true);
						}
					}
					if (Vars.usuario == null) {
						Log.d("Progress", "main Dismiss");
						leftRightSlidingMenu.setSlidingEnabled(true);
						ivTitleBtnLeft.setVisibility(View.VISIBLE);
						badgeBtnRight.setVisibility(View.VISIBLE);
						FragmentManager manager = getFragmentManager();
						android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
						if (frag != null) {
							manager.beginTransaction().remove(frag).commit();
						}
						DialogFragmentWS editNameDialog = new DialogFragmentWS(true, "Conexión fallida", "No se ha podido establecer conexión con el servidor, refresque los listados más tarde. si el problema persiste comuniquese con el Administrador del sistema.", "Aceptar", true, 0);
						editNameDialog.show(manager, "fragment_edit_name");
					} else {
						if (Util.isConnect(activity)) {
							//todo error
							new ConceptosTask().execute((Void) null);
						}
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class ConceptosTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

//			try {
				int proceso = ProcesosDelSistema.RecogidaEnCliente.getId();
				String id_operador = Vars.usuario.getObjeto().getId_OperadorLogistico().get_id();

				userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				String Conceptos = userDetails.getString("Conceptos", "");

				if (Conceptos.equals("")) {
					Util.getConcepto(getBaseContext(), proceso, id_operador);
				} else {
					Gson gson = new Gson();
					Vars.conceptoProceso = gson.fromJson(Conceptos, ConceptoProceso[].class);
				}
				return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				progressDialog.dismiss();
				if (Util.isConnect(activity)) {
					new FormatoTask().execute((Void) null);
				}
			}
		}

		@Override
		protected void onCancelled() {
		}
	}

	@SuppressLint("StaticFieldLeak")
	public class FormatoTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String Formatos = userDetails.getString("Formatos", "");

			if (Formatos.equals("")) {
				Util.getFormato(getBaseContext(), Vars.usuario.getObjeto().getId_OperadorLogistico().get_id(), "");
			} else {
				Gson gson = new Gson();
				Vars.formato = gson.fromJson(Formatos, Formato[].class);
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {
				Vars.inMain = false;
				progressDialog.dismiss();
				Log.d("Progress", "main Dismiss");
				leftRightSlidingMenu.setSlidingEnabled(true);
				ivTitleBtnLeft.setVisibility(View.VISIBLE);
				badgeBtnRight.setVisibility(View.VISIBLE);
				if (Vars.formato != null) {
					if (Vars.pn_id_orden == null || Vars.pn_id_orden.equals("")) {
						push = false;
						Log.d("Main", "Push formato false");
					} else {
						push = true;
						if (Vars.pn_tipo.equals("orden")) {
							if (Util.isConnect(activity)) {
								new OrdenServicioTask().execute((Void) null);
							}
						} else {
							Fragment newContent = new RuteoFragment();
							ivTitleMenu.setVisibility(View.VISIBLE);

							ivTitleName.setText("Lista de ruteo");

							if (newContent != null)
								LeftSlidingMenuFragment.switchFragment(activity, newContent);
						}
						Log.d("Main", "Push formato true");
					}
				}
			}
		}

		@Override
		protected void onCancelled() {
		}
	}

	private void register(final String email, final String password){

		GCMClientManager pushClientManager = new GCMClientManager(this, getResources().getString(R.string.gcm_defaultSenderId));
		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
			@Override
			public void onSuccess(String registrationId, boolean isNewRegistration) {
//                mProgressView.dismiss();
				Log.d("Registration id", registrationId);
				if (Util.isConnect(activity)) {
					new ObjetoLoginTask(email, password, registrationId).execute((Void) null);
				}
				//send this registrationId to your server
			}

			@Override
			public void onFailure(String ex) {
				super.onFailure(ex);
				android.app.FragmentManager manager = getFragmentManager();
				android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
				if (frag != null) {
					manager.beginTransaction().remove(frag).commit();
				}
				DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
				editNameDialog.show(manager, "fragment_edit_name");

//                ad.setMessage(getString(R.string.RegisterError));
//                ad.show();
//                progressBar.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Vars.resOS){
//			leftRightSlidingMenu.setSlidingEnabled(false);
//			ivTitleBtnLeft.setVisibility(View.GONE);
//			badgeBtnRight.setVisibility(View.GONE);
			if (Util.isConnect(activity)) {
				new OrdenesServicioFragment.OrdenServicioTask(this).execute((Void) null);
			}
		}

		if (resultCode == Vars.resMO){
//			leftRightSlidingMenu.setSlidingEnabled(false);
//			ivTitleBtnLeft.setVisibility(View.GONE);
//			badgeBtnRight.setVisibility(View.GONE);
			if (Util.isConnect(this)) {
				new RuteoFragment.ManifiestoDetalleTask(Vars.usuario.getObjeto().get_id(), this).execute((Void) null);
			}
		}

		if (resultCode == Vars.resCI){
//			leftRightSlidingMenu.setSlidingEnabled(false);
//			ivTitleBtnLeft.setVisibility(View.GONE);
//			badgeBtnRight.setVisibility(View.GONE);
			if (Util.isConnect(this)) {

				if (resultCode == Vars.resCS){	new OrdenServicioTask().execute((Void) null);
			}
		}

		}

////		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//		if (requestCode == IntentIntegrator.REQUEST_CODE ){
//			if (data != null) {
//				if (data.getContents() == null) {
//					Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
//				} else {
//					Toast.makeText(this, "Código escaneado: " + result.getContents(), Toast.LENGTH_LONG).show();
//					Vars.idMovilizado = Integer.parseInt(result.getContents());
//					BarcodeFragment.et_ab_texto.setText(String.valueOf(Vars.idMovilizado));
//				}
//			}
//		}



		if (requestCode == IntentIntegrator.REQUEST_CODE) {
			if (data != null) {
					String contents = data.getStringExtra("SCAN_RESULT");
	//				edit.setText(format);
	//				edit2.setText(contents);

					Toast.makeText(this, "Código escaneado: " + contents, Toast.LENGTH_LONG).show();

				try {
					Vars.idMovilizado = Integer.parseInt(contents);
				}catch(NumberFormatException nfe){
					Vars.idMovilizado = 0;
				}

				BarcodeFragment.et_ab_texto.setText(String.valueOf(Vars.idMovilizado));
				} else {
					Toast.makeText(this, "Operación cancelada", Toast.LENGTH_LONG).show();
	//				edit.setText("barkod");
	//				edit2.setText("vrsta barkoda");
				}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1:
				LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				if (!gpsEnabled) {
					FragmentManager manager = getFragmentManager();
					android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
					if (frag != null) {
						manager.beginTransaction().remove(frag).commit();
					}
					DialogFragmentWS editNameDialog = new DialogFragmentWS(true, "Activar GPS", "Es necesario activar el GPS para usar esta App.", "Aceptar", true, 123);
					editNameDialog.show(manager, "fragment_edit_name");
				}
//				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;
			case 2:
				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;
			case 3:
				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;
			case 4:
				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;
			case 5:
				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;
			case 6:
				permisos();
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

				} else {
				}
				break;

		}
	}

	public void ScanBarcode(){
		try {

			//start the scanning activity from the com.google.zxing.client.android.SCAN intent
			integrator = new IntentIntegrator(this);
			integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
			integrator.setPrompt("ScanBarcode");
			integrator.setCameraId(0);  // Use a specific camera of the device
			integrator.setBeepEnabled(true);
			integrator.setOrientationLocked(true);
			integrator.setCaptureActivity(CaptureActivityPortrait.class);
			integrator.initiateScan();


        /*Intent intent = new Intent(ACTION_SCAN);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);*/
		} catch (ActivityNotFoundException anfe) {
			//on catch, show the download dialog
			Toast.makeText(this, "Algo esta mal", Toast.LENGTH_SHORT).show();
			//showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Vars.formato != null) {
			if (Vars.pn_id_orden == null || Vars.pn_id_orden.equals("")) {
				push = false;
				Log.d("Main", "Push false");
			} else {
				push = true;
				if (Vars.pn_tipo.equals("orden")) {
					if (Util.isConnect(this)) {
						new OrdenServicioTask().execute((Void) null);
					}
				} else {
					Fragment newContent = new RuteoFragment();
					ivTitleMenu.setVisibility(View.VISIBLE);

					ivTitleName.setText("Lista de ruteo");

					if (newContent != null)
						LeftSlidingMenuFragment.switchFragment(this, newContent);
				}
				Log.d("Main", "Push true");
			}
		}
	}
}
