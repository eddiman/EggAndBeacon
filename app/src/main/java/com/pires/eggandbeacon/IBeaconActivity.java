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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pires.eggandbeacon.adapter.BeaconAdapter;

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

    @BindView(R.id.beacon_grid) GridView gridBeacon;

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
                    logToDisplay(beacons);


                    Log.i(TAG, "BTname: " + beacon.getBluetoothName() + " UUID: " + beacon.getId1() + " Major: " + beacon.getId2()+ " Minor: " + beacon.getId3()
                            + " RSSI: " + beacon.getRssi() + " distance "+ beacon.getDistance()+" meters \n");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }


    private void logToDisplay(final Collection<Beacon> beacons) {
        runOnUiThread(new Runnable() {
            public void run() {
                BeaconAdapter beaconAdapter = new BeaconAdapter(getApplicationContext(), beacons);
                gridBeacon.setOnScrollListener(new AbsListView.OnScrollListener() {

                    private int currentVisibleItemCount;
                    private int currentScrollState;
                    private int currentFirstVisibleItem;
                    private int totalItem;
                    private LinearLayout lBelow;


                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        // TODO Auto-generated method stub
                        this.currentScrollState = scrollState;
                        this.isScrollCompleted();
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem,
                                         int visibleItemCount, int totalItemCount) {
                        // TODO Auto-generated method stub
                        this.currentFirstVisibleItem = firstVisibleItem;
                        this.currentVisibleItemCount = visibleItemCount;
                        this.totalItem = totalItemCount;


                    }

                    private void isScrollCompleted() {
                        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                && this.currentScrollState == SCROLL_STATE_IDLE) {
                            /** To do code here*/


                        }

                    }
                });
                // beaconAdapter.notifyDataSetChanged();
                gridBeacon.setAdapter(beaconAdapter);


            }
        });

    }
}