package com.pires.eggandbeacon;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IBeaconActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    @BindView(R.id.alt_uuid) TextView textUuid;
    @BindView(R.id.alt_Major) TextView textMajor;
    @BindView(R.id.alt_Minor) TextView textMinor;
    @BindView(R.id.alt_rssi) TextView textRssi;
    @BindView(R.id.alt_dist) TextView textDist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ibeacon);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().clear();
        beaconManager.setBackgroundScanPeriod(1100);
        beaconManager.setForegroundScanPeriod(1100);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.bind(this);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {

                    Beacon beacon = beacons.iterator().next();
                    logToDisplay(beacon);


                    Log.i(TAG, "BTname: " + beacon.getBluetoothName() + " UUID: " + beacon.getId1() + " Major: " + beacon.getId2()+ " Minor: " + beacon.getId3()
                            + " RSSI: " + beacon.getRssi() + " distance "+ beacon.getDistance()+" meters \n");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    private void logToDisplay(final Beacon beacon) {
        runOnUiThread(new Runnable() {
            public void run() {
                textUuid.setText( "UUID: " + beacon.getId1());
                textMajor.setText("Major: " + beacon.getId2());
                textMinor.setText("Minor: " + beacon.getId3());

                textRssi.setText("RSSI: " + beacon.getRssi());
                textDist.setText("Distance "+ beacon.getDistance()+ " meters");
            }
        });
    }
}

