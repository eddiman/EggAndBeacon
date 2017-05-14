package com.pires.eggandbeacon;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import com.pires.eggandbeacon.model.Ibeacon;
import com.pires.eggandbeacon.util.UuidConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private final int SCAN_PERIOD = 20000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    UuidConverter uuidConv = new UuidConverter();

    //Butterknife binders
    @BindView(R.id.text_view) TextView textView;
    @BindView(R.id.start_le_scan) Button btnStartLeScan;
    @BindView(R.id.stop_le_scan) Button btnStopLeScan;
    @BindView(R.id.go_to_indoor) Button btnGoToIndoor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        askForLocationPermission();

        initGUI();


    }

    @OnClick(R.id.start_le_scan)
    public void startScan(Button button){
        mHandler.post(scanRunnable);
        button.setText("Scanning...");
    }

    @OnClick(R.id.stop_le_scan)
    public void stopScan(Button button){
        mHandler.removeCallbacks(scanRunnable);
        scanLeDevice(false);
        //button.setText("Scanning...");
    }

    @OnClick(R.id.go_to_indoor)
    public void goToIndoor(Button button){
        Intent i = new Intent(this, IndoorActivity.class);
        startActivity(i);
    }

    private void initGUI() {
        ButterKnife.bind(this);
    }

    private void askForLocationPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);


                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
        }

    }


    private ScanCallback mLeScanCallback = new ScanCallback() {



        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            try {
                Ibeacon beacon = uuidConv.createIbeaconFromRecord(result.getScanRecord().getBytes());

                textView.append("UUID: " + beacon.getUuid() + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " RSSI: " + result.getRssi() + "\n" + "\n");
                Log.d("iBeacon devices", "UUID: " + beacon.getUuid() + " Major: " + beacon.getMajor() + " Minor: " + beacon.getMinor() + " RSSI: " + result.getRssi());


            } catch (NullPointerException e){
                //Log.d("BLE Devices", "Name " + result.getDevice().getName() + " RSSI " + result.getRssi() + " Class: " + result.getDevice().getBluetoothClass());

            }





            super.onScanResult(callbackType, result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private void scanLeDevice(final boolean enable) {

        final BluetoothLeScanner bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            /*mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;

                    bluetoothLeScanner.stopScan(mLeScanCallback);
                    btnStartLeScan.setText("Scan for BLE devices");
                }
            }, SCAN_PERIOD);*/
            mScanning = true;
            bluetoothLeScanner.startScan(mLeScanCallback);



        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(mLeScanCallback);
            btnStartLeScan.setText("Scan for BLE devices");
        }
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanLeDevice(true);
        }

    };

}
