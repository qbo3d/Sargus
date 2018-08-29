package com.qbo3d.sargus.Activities;

import android.Manifest;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.qbo3d.sargus.DialogFragment.DialogFragmentVal;
import com.qbo3d.sargus.Fragments.LeftSlidingMenuFragment;
import com.qbo3d.sargus.Fragments.ScanningFragment;
import com.qbo3d.sargus.Fragments.TicketFragment;
import com.qbo3d.sargus.Objects.Usuario;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Utilities.GCMClientManager;
import com.qbo3d.sargus.Utilities.Serv;
import com.qbo3d.sargus.Vars;

public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener,
		DialogFragmentVal.Respuesta {

	public static SlidingMenu leftRightSlidingMenu;
	public static ImageButton ib_tm_hamburguesa;
	public static TextView tv_tm_title;
	public static ImageButton ib_tm_menu;
	private Fragment mContent;

	public static View mFragmentFormView;

	public static ProgressDialog progressDialog;

	public static Button bt_tm_on;
	public static Button bt_tm_off;
	public static ProgressBar pb_tm_indicador;

	private Activity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = new Intent();
		String packageName = getPackageName();
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		if (!pm.isIgnoringBatteryOptimizations(packageName)) {
			intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
			intent.setData(Uri.parse("package:" + packageName));
			startActivity(intent);
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

		tv_tm_title.setText(getResources().getString(R.string.lsm_ticket));

		String documento;
		String password;

		if (TextUtils.isEmpty(Vars.documento) && TextUtils.isEmpty(Vars.password)) {
			documento = Util.getStorageString(getBaseContext(),"documento");
			password = Util.getStorageString(getBaseContext(),"password");
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

	private void callScanning() {
		Intent intent = new Intent(this, ScanningFragment.class);
		startActivity(intent);
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
		mContent = new TicketFragment();
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
			case R.id.ib_tm_menu:
//				Toast.makeText(activity, "Menu", Toast.LENGTH_SHORT).show();
				callScanning();
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
			DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
			editNameDialog.show(manager, "fragment_edit_name");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onFinishVal(int result, boolean op) {

		if (op) {

		}
	}

	public class LoginTask extends AsyncTask<Void, Void, Boolean> {

		private Activity mActivity;
		private final String mDocumento;
		private final String mPassword;
		private final String mId;

		LoginTask(Activity activity, String documento, String password, String Id) {
			mActivity = activity;
			mDocumento = documento;
			mPassword = password;
			mId = Id;
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
				Vars.isConn = Serv.getLogin(mActivity, mDocumento, mPassword, mId);
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
						new AllTicketTask().execute();
					}
					LeftSlidingMenuFragment.tv_fml_usuario.setText(Vars.usuario.getNombre());
					LeftSlidingMenuFragment.tv_fml_proyecto.setText(Vars.usuario.getEntidad());
				}
			} else {
				Util.disconnectMessage(mActivity, mActivity.getLocalClassName());
			}
		}
	}

	public class AllTicketTask extends AsyncTask<Void, Void, Boolean> {

		AllTicketTask() {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			Vars.isConn = Serv.getAllTicket();

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			progressDialog.dismiss();
			if (success && Vars.isConn) {
				if (Vars.allTicket != null) {
					if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
						new EntitiesUserTask().execute();
						Util.cargarAllTickets(mActivity, TicketFragment.lv_ft_ticket, R.layout.itemlist_ticket, Util.ticketListToData());
					}
				}
			} else {
				Util.disconnectMessage(mActivity, mActivity.getLocalClassName());
			}
		}
	}

	public class EntitiesUserTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Vars.isConn = Serv.getEntitiesUser();
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			progressDialog.dismiss();
			if (success && Vars.isConn) {
				if (Vars.entidad != null) {
					if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
						new GroupsEntityUserTask().execute();
					}
				}
			} else {
				Util.disconnectMessage(mActivity, mActivity.getLocalClassName());
			}
		}
	}

	public class GroupsEntityUserTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Vars.isConn = Serv.getGroupsEntity();
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			progressDialog.dismiss();
			if (success && Vars.isConn) {
				if (Vars.grupos != null) {
					if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
					}
				}
			} else {
				Util.disconnectMessage(mActivity, mActivity.getLocalClassName());
			}
		}
	}

//	public class DownloadImagesTask extends AsyncTask<Void, Void, Bitmap> {
//
//		@Override
//		protected Bitmap doInBackground(Void... params) {
//
//			try {
//				if (new File(Util.pathMainFolfer  + File.separator + Util.usuario.getPicture()).exists()) {
//					FileInputStream streamIn = new FileInputStream(Util.pathMainFolfer  + File.separator + Util.usuario.getNombreLogo());
//					Util.logo = BitmapFactory.decodeStream(streamIn);
//				} else {
//
//					Util.logo = download_Image(Util.picturesGLPI + Util.usuario.getPicture());
//
//					if (Util.logo == null) {
//						Util.logo = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//					}
//
////			Almacenamiento de bitmap
//
//					OutputStream fOutputStream;
//					File file = new File(Util.pathMainFolfer, Util.usuario.getNombreLogo());
//					fOutputStream = new FileOutputStream(file);
//
//					Util.logo.compress(Bitmap.CompressFormat.PNG, 100, fOutputStream);
//
//					fOutputStream.flush();
//					fOutputStream.close();
//
//					MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				//Toast.makeText(activity, "Save Failed", Toast.LENGTH_SHORT).show();
//			}
//
//			return Util.logo;
//		}
//
//		@Override
//		protected void onPostExecute(Bitmap result) {
//			if (result != null) {
//				LeftSlidingMenuFragment.iv_fml_logo.setImageBitmap(result);
//				Bitmap roundedCornersImage = ImageHelper.getRoundedCornerBitmap(result, 104);
//				LeftSlidingMenuFragment.iv_fml_logo.setImageBitmap(roundedCornersImage);
//			}
//		}
//
//		private Bitmap download_Image(String url) {
//			Bitmap bitmap = null;
//			InputStream stream = null;
//			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//			bmOptions.inSampleSize = 1;
//
//			try {
//				stream = getHttpConnection(url);
//				if (stream != null) {
//					bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
//					stream.close();
//				}
//			}
//			catch (IOException e1) {
//				e1.printStackTrace();
//				System.out.println("downloadImage"+ e1.toString());
//			}
//			return bitmap;
//		}
//
//		public InputStream getHttpConnection(String urlString)  throws IOException {
//
//			InputStream stream = null;
//			URL url = new URL(urlString);
//			URLConnection connection = url.openConnection();
//
//			try {
//				HttpURLConnection httpConnection = (HttpURLConnection) connection;
//				httpConnection.setRequestMethod("GET");
//				httpConnection.connect();
//
//				if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//					stream = httpConnection.getInputStream();
//				}
//			}
//			catch (Exception ex) {
//				ex.printStackTrace();
//				System.out.println("downloadImage" + ex.toString());
//			}
//			return stream;
//		}
//	}

//	private void register(final String email, final String password){
//
//		GCMClientManager pushClientManager = new GCMClientManager(this, getResources().getString(R.string.gcm_defaultSenderId));
//		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
//			@Override
//			public void onSuccess(String registrationId, boolean isNewRegistration) {
//				Log.d("Registration id", registrationId);
//				if (Util.isConnect(activity)) {
//					new ObjetoLoginTask(email, password, registrationId).execute((Void) null);
//				}
//				//send this registrationId to your server
//			}
//
//			@Override
//			public void onFailure(String ex) {
//				super.onFailure(ex);
//				android.app.FragmentManager manager = getFragmentManager();
//				android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
//				if (frag != null) {
//					manager.beginTransaction().remove(frag).commit();
//				}
//				DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
//				editNameDialog.show(manager, "fragment_edit_name");
//
//			}
//		});
//	}

	private void register(final String documento, final String password) {

		GCMClientManager pushClientManager = new GCMClientManager(this, getResources().getString(R.string.gcm_defaultSenderId));
		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
			@Override
			public void onSuccess(String registrationId, boolean isNewRegistration) {
//                mProgressView.dismiss();
				Log.d("Registration id", registrationId);

				if (Util.isConnect(mActivity, mActivity.getLocalClassName())) {
					new LoginTask(mActivity, documento, password, registrationId).execute((Void) null);
				}
				//send this registrationId to your server
			}

			@Override
			public void onFailure(String ex) {
				super.onFailure(ex);
				android.app.FragmentManager manager = getFragmentManager();
				android.app.Fragment frag = manager.findFragmentByTag(getString(R.string.df_edit_name));
				if (frag != null) {
					manager.beginTransaction().remove(frag).commit();
				}
				DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", "");
				editNameDialog.show(manager, getString(R.string.df_edit_name));

//                ad.setMessage(getString(R.string.RegisterError));
//                ad.show();
//                progressBar.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ScanningFragment.ENABLE_BT_REQUEST_ID) {
			if (resultCode == Activity.RESULT_CANCELED) {
				ScanningFragment.btDisabled(mActivity);
			}
		}

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
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

		switch (requestCode) {
			case Vars.PERMISSIONS_MULTIPLE_REQUEST:
				if (grantResults.length > 0) {
					boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
					boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

					if(cameraPermission && readExternalFile)
					{
						// write your logic here
					} else {
						Snackbar.make(this.findViewById(android.R.id.content),
								"Please Grant Permissions to upload profile photo",
								Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
											requestPermissions(
													new String[]{Manifest.permission
															.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
													Vars.PERMISSIONS_MULTIPLE_REQUEST);
									}
								}).show();
					}
				}
				break;
		}
	}

	private void checkPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
				ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
				ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
					ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.CAMERA) ||
					ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.ACCESS_FINE_LOCATION)) {

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
													Manifest.permission.CAMERA,
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
									Manifest.permission.CAMERA,
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
