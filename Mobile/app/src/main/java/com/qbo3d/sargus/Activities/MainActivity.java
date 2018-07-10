package com.qbo3d.sargus.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.qbo3d.sargus.DialogFragment.DialogFragmentVal;
import com.qbo3d.sargus.Fragments.PrincipalFragment;
import com.qbo3d.sargus.Fragments.Sliding.LeftSlidingMenuFragment;
import com.qbo3d.sargus.Fragments.Sliding.RightSlidingMenuFragment;
import com.qbo3d.sargus.Objects.Usuario;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Utilities.GCMClientManager;
import com.qbo3d.sargus.Utilities.ImageHelper;
import com.qbo3d.sargus.Utilities.Serv;
import com.qbo3d.sargus.Utilities.Util;
import com.qbo3d.sargus.Utilities.Vars;

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

public class MainActivity extends SlidingFragmentActivity implements
        OnClickListener,
		DialogFragmentVal.Respuesta {

	public static SlidingMenu leftRightSlidingMenu;
	public static ImageButton ivTitleBtnLeft;
	public static TextView ivTitleName;
	public static ImageButton badgeBtnRight;
	private Fragment mContent;
	public static ImageButton ivTitleMenu;

	public static View mFragmentFormView;

	public static ProgressDialog progressDialog;

	private Activity activity;

	public static final int PERMISSIONS_MULTIPLE_REQUEST = 123;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Util.createFolders(this);

		progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.pd_cargando));
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		initLeftRightSlidingMenu();
		setContentView(R.layout.activity_main);

		activity = this;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			Intent intent = new Intent();
			String packageName = getPackageName();
			PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
			if (!pm.isIgnoringBatteryOptimizations(packageName)) {
				intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
				intent.setData(Uri.parse("package:" + packageName));
				startActivity(intent);
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			checkPermission();

		} else {
			// write your logic here
		}

		initView();

		ivTitleName.setText("Item 1");

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
	}

	private void callLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		mFragmentFormView = findViewById(R.id.content_frame);
		ivTitleBtnLeft = (ImageButton) this.findViewById(R.id.ivTitleBtnLeft);
		badgeBtnRight = (ImageButton) this.findViewById(R.id.badgeBtnRight);
		ivTitleName = (TextView) this.findViewById(R.id.ivTitleName);
		ivTitleBtnLeft.setOnClickListener(this);
		badgeBtnRight.setOnClickListener(this);
	}

	private void initLeftRightSlidingMenu() {
		mContent = new PrincipalFragment();
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
			String usuario = Util.getStorageString(getBaseContext(), "usuario");

			if (usuario == null || usuario.equals("")) {
				Vars.isConn = Serv.getLogin(activity, mDocumento, mPassword, mId);
			} else {
				Vars.isConn = true;
				Gson gson = new Gson();
				Vars.usuario = gson.fromJson(usuario, Usuario.class);
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			progressDialog.dismiss();
			if (success && Vars.isConn) {
				if (Vars.usuario != null) {
					if (Util.isConnect(activity, activity.getLocalClassName())) {
//						new OrdenServicioTask().execute((Void) null);
//						if (Vars.usuario != null) {
//							LeftSlidingMenuFragment.tv_fml_operador.setText(Vars.usuario.getObjeto().getId_OperadorLogistico().getNombre());
//							LeftSlidingMenuFragment.tv_fml_usuario.setText(Vars.usuario.getObjeto().getNombre());
//						}
						new DownloadImagesTask().execute();
					}
				}
			} else {
				Util.disconnectMessage(activity, activity.getLocalClassName());
			}
		}
	}

	public class DownloadImagesTask extends AsyncTask<Void, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Void... params) {

			try {
				if (new File(Vars.pathMainFolfer  + File.separator + Vars.usuario.getPicture()).exists()) {
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
			} catch (IOException e) {
				e.printStackTrace();
				//Toast.makeText(activity, "Save Failed", Toast.LENGTH_SHORT).show();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
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
				if (stream != null) {
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

				if (Util.isConnect(activity, activity.getLocalClassName())) {
					new ObjetoLoginTask(documento, password, registrationId).execute((Void) null);
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
				DialogFragmentVal editNameDialog = new DialogFragmentVal(Vars.resCX, getString(R.string.login_connection_error), getString(R.string.login_connection_error_content),"Ok", getString(R.string.osa_vacio));
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
			case PERMISSIONS_MULTIPLE_REQUEST:
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
										if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
											requestPermissions(
													new String[]{Manifest.permission
															.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
													PERMISSIONS_MULTIPLE_REQUEST);
										}
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
											}, PERMISSIONS_MULTIPLE_REQUEST);
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
							}, PERMISSIONS_MULTIPLE_REQUEST);
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
