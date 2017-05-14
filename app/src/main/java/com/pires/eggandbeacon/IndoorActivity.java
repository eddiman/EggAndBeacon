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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.pires.eggandbeacon.model.Ibeacon;
import com.pires.eggandbeacon.util.UuidConverter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IndoorActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning = false;
    private Handler mHandler = new Handler();
    private final int SCAN_PERIOD = 20000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 1;
    UuidConverter uuidConv = new UuidConverter();

    //Butterknife binders
    @BindView(R.id.start_indoor_scan) Button btnStartIndoorScan;
    @BindView(R.id.stop_indoor_scan) Button btnStopIndoorScan;
    @BindView(R.id.go_to_main) Button btnGoToMain;
    @BindView(R.id.img_circle_living) ImageView imgCircleLiving;
    @BindView(R.id.img_marker_living) ImageView imgMarkerLiving;
    @BindView(R.id.img_circle_hall) ImageView imgCircleHall;
    @BindView(R.id.img_marker_hall) ImageView imgMarkerHall;
    @BindView(R.id.area) TextView textArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor);
        BluetoothManager bluetoothManager = (BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        askForLocationPermission();

        initGUI();


    }

    @OnClick(R.id.start_indoor_scan)
    public void startIndoorScan(Button button){
        mHandler.post(scanRunnable);
        button.setText("Scanning...");
    }

    @OnClick(R.id.go_to_main)
    public void goToMain(Button button){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.stop_indoor_scan)
    public void stopIndoorScan(Button button){
        mHandler.removeCallbacks(scanRunnable);
        scanLeDevice(false);
        //button.setText("Scanning...");
    }

    private void initGUI() {
        ButterKnife.bind(this);


    }

    private void askForLocationPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(IndoorActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(IndoorActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(IndoorActivity.this,
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


                if(beacon.getUuid().equals("B9407F30-F5F8-466E-AFF9-25556B57FE6D") && beacon.getMajor() == 59345 && beacon.getMinor() == 17776 && result.getRssi() < -50){
                    Log.d("RSSI ", "" + result.getRssi());
                    textArea.setText("Du er i: Stuen");
                    imgMarkerLiving.setVisibility(View.VISIBLE);
                    imgCircleLiving.setVisibility(View.VISIBLE);
                } else {
                    imgMarkerLiving.setVisibility(View.INVISIBLE);
                    imgCircleLiving.setVisibility(View.INVISIBLE);
                }

                if(beacon.getUuid().equals("B9407F30-F5F8-466E-AFF9-25556B57FE6D") && beacon.getMajor() == 37651 && beacon.getMinor() == 13436 && result.getRssi() < -50){
                    Log.d("RSSI ", "" + result.getRssi());
                    imgMarkerHall.setVisibility(View.VISIBLE);
                    imgCircleHall.setVisibility(View.VISIBLE);
                    textArea.setText("Du er i: Gangen");
                } else {
                    imgMarkerHall.setVisibility(View.INVISIBLE);
                    imgCircleHall.setVisibility(View.INVISIBLE);
                }


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
            btnStartIndoorScan.setText("Scan for BLE devices");
        }
    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            scanLeDevice(true);
        }

    };

}
