package com.qbo3d.sargus.Fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.qbo3d.sargus.Activities.MainActivity;
import com.qbo3d.sargus.Activities.PeripheralActivity;
import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Utilities.BLE.BleWrapper;
import com.qbo3d.sargus.Utilities.BLE.BleWrapperUiCallbacks;
import com.qbo3d.sargus.Utilities.BLE.DeviceListAdapter;

public class ScanningFragment extends Fragment {

    private static final long SCANNING_TIMEOUT = 5 * 1000; /* 5 seconds */
    public static final int ENABLE_BT_REQUEST_ID = 1;

    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private DeviceListAdapter mDevicesListAdapter = null;
    private BleWrapper mBleWrapper = null;
    private Activity mActivity;

    private ListView lv_as;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ble_fragment_scanning, container, false);


        lv_as = view.findViewById(R.id.lv_as);

        mActivity = getActivity();

        // create BleWrapper with empty callback object except uiDeficeFound function (we need only that here)
        mBleWrapper = new BleWrapper(mActivity, new BleWrapperUiCallbacks.Null() {
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

        return view;
    }

    @Override
    public void onResume() {
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

        mDevicesListAdapter = new DeviceListAdapter(mActivity);
        lv_as.setAdapter(mDevicesListAdapter);

        // Automatically start scanning for devices
        mScanning = true;
        // remember to add timeout for scanning to not run it forever and drain the battery
        addScanningTimeout();
        mBleWrapper.startScanning();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScanning = false;
        mBleWrapper.stopScanning();

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
//
    }

    /* check if user agreed to enable BT */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // user didn't want to turn on BT
        if (requestCode == ENABLE_BT_REQUEST_ID) {
            if (resultCode == Activity.RESULT_CANCELED) {
                btDisabled(mActivity);
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
            }
        };
        mHandler.postDelayed(timeout, SCANNING_TIMEOUT);
    }

    /* add device to the current list of devices */
    private void handleFoundDevice(final BluetoothDevice device,
                                   final int rssi,
                                   final byte[] scanRecord) {
        // adding to the UI have to happen in UI thread
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDevicesListAdapter.addDevice(device, rssi, scanRecord);
                mDevicesListAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void btDisabled(Activity mActivity) {
        Toast.makeText(mActivity, "Sorry, BT has to be turned ON for us to work!", Toast.LENGTH_LONG).show();
    }

    private void bleMissing() {
        Toast.makeText(mActivity, "BLE Hardware is required but not available!", Toast.LENGTH_LONG).show();
    }
}
