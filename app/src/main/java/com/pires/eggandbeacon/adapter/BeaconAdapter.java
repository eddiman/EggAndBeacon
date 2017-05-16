package com.pires.eggandbeacon.adapter;

/**
 * Created by EddiStat on 16.05.2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pires.eggandbeacon.R;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;

public class BeaconAdapter extends BaseAdapter {

    private final Context mContext;
    private final Collection<Beacon> beacons;

    // 1
    public BeaconAdapter(Context context, Collection<Beacon> beacons) {
        this.mContext = context;
        this.beacons = beacons;
    }

    // 2
    @Override
    public int getCount() {
        return beacons.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1
        List<Beacon> list = new ArrayList<>(beacons);

        final Beacon beacon = list.get(position);

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.gridview_beacons, null);
        }

        final TextView textUuid = (TextView)convertView.findViewById(R.id.alt_uuid);
        final TextView textMajor = (TextView)convertView.findViewById(R.id.alt_Major);
        final TextView textMinor = (TextView)convertView.findViewById(R.id.alt_Minor);
        final TextView textRssi = (TextView)convertView.findViewById(R.id.alt_rssi);
        final TextView textDist = (TextView)convertView.findViewById(R.id.alt_dist);

        if (list.size() > 0) {
            textUuid.setText("UUID: " + beacon.getId1());
            textMajor.setText("Major: " + beacon.getId2());
            textMinor.setText("Minor: " + beacon.getId3());

            textRssi.setText("RSSI: " + beacon.getRssi());
            textDist.setText("Distance " + beacon.getDistance() + " meters");
        } else {
            textUuid.setText("There are no beacons");
            textMajor.setText("Major: " );
            textMinor.setText("Minor: ");

            textRssi.setText("RSSI: " );
            textDist.setText("Distance ");
        }
        return convertView;
    }


}
