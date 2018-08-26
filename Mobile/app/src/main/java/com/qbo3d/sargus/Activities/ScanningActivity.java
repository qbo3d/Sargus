package com.qbo3d.sargus.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qbo3d.sargus.Activities.PeripheralActivity;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Utilities.BLE.BleWrapper;
import com.qbo3d.sargus.Utilities.BLE.BleWrapperUiCallbacks;
import com.qbo3d.sargus.Utilities.BLE.DeviceListAdapter;

public class ScanningActivity extends Activity {

    private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
    private static final int ENABLE_BT_REQUEST_ID = 1;

    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private DeviceListAdapter mDevicesListAdapter = null;
    private BleWrapper mBleWrapper = null;
    private Activity mActivity;

    private ListView lv_as;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ble_activity_scanning);

        lv_as = findViewById(R.id.lv_as);

        mActivity = this;

        // create BleWrapper with empty callback object except uiDeficeFound function (we need only that here) 
        mBleWrapper = new BleWrapper(this, new BleWrapperUiCallbacks.Null() {
            @Override
            public void uiDeviceFound(final BluetoothDevice device, final int rssi, final byte[] record) {
                handleFoundDevice(device, rssi, record);
            }
        });

        // check if we have BT and BLE on board
        if (!mBleWrapper.checkBleHardwareAvailable()) {
            bleMissing();
        }

        lv_as.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final BluetoothDevice device = mDevicesListAdapter.getDevice(position);
                if (device == null) return;

                final Intent intent = new Intent(mActivity, PeripheralActivity.class);
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_NAME, device.getName());
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
                intent.putExtra(PeripheralActivity.EXTRAS_DEVICE_RSSI, mDevicesListAdapter.getRssi(position));

                if (mScanning) {
                    mScanning = false;
                    invalidateOptionsMenu();
                    mBleWrapper.stopScanning();
                }

                startActivity(intent);
            }
        });

        MainActivity.bt_tm_on.setVisibility(View.VISIBLE);
        MainActivity.bt_tm_off.setVisibility(View.VISIBLE);

        MainActivity.bt_tm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanning = true;
                mBleWrapper.startScanning();
                menuDer();
            }
        });

        MainActivity.bt_tm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mScanning = false;
                mBleWrapper.stopScanning();
                menuDer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on every Resume check if BT is enabled (user could turn it off while app was in background etc.)
        if (!mBleWrapper.isBtEnabled()) {
            // BT is not turned on - ask user to make it enabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST_ID);
            // see onActivityResult to check what is the status of our request
        }

        // initialize BleWrapper object
        mBleWrapper.initialize();

        mDevicesListAdapter = new DeviceListAdapter(this);
        lv_as.setAdapter(mDevicesListAdapter);

        // Automatically start scanning for devices
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();

        invalidateOptionsMenu();
    }

    ;

    @Override
    protected void onPause() {
        super.onPause();
        mScanning = false;
        mBleWrapper.stopScanning();
        invalidateOptionsMenu();

        mDevicesListAdapter.clearList();
    }

    public void menuDer() {

        MainActivity.bt_tm_on.setText("Start");
        MainActivity.bt_tm_off.setText("Stop");
//        if (mScanning) {
//            MainActivity.bt_tm_on.setVisibility(View.GONE);
//            MainActivity.bt_tm_off.setVisibility(View.VISIBLE);
//            MainActivity.pb_tm_indicador.setVisibility(View.VISIBLE);
//
//        } else {
//            MainActivity.bt_tm_on.setVisibility(View.VISIBLE);
//            MainActivity.bt_tm_off.setVisibility(View.GONE);
//            MainActivity.pb_tm_indicador.setVisibility(View.GONE);
//        }
    }

    /* check if user agreed to enable BT */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                btDisabled();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* make sure that potential scanning will take no longer
     * than <SCANNING_TIMEOUT> seconds from now on */
    private void addScanningTimeout() {
        Runnable timeout = new Runnable() {
            @Override
            public void run() {
                if (mBleWrapper == null) return;
                mScanning = false;
                mBleWrapper.stopScanning();
                invalidateOptionsMenu();
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }

    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord) {
        // adding to the UI have to happen in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                mDevicesListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void btDisabled() {
        Toast.makeText(this, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void bleMissing() {
        Toast.makeText(this, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
        finish();
    }
}
