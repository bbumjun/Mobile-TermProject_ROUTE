package com.termproject.route.route;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class mapActivity extends Activity {
    ListView listView;
    ItemListAdapter adapter;
    List<OneItem> items;
    int cnt = 0;

    public class OneItem {
        String time;
        String content;
        public OneItem(String time, String content) {
            this.time = time;
            this.content = content;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button01 = (Button) findViewById(R.id.button01);
        button01.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLocationService();
            }
        });

        items =  new ArrayList<OneItem>();

        listView = (ListView)findViewById(R.id.listView);
        adapter = new ItemListAdapter(this, R.layout.item_layout, items);
        listView.setAdapter(adapter);
    }

    class ItemListAdapter extends ArrayAdapter<OneItem> {
        private List<OneItem> items;
        private Context context;
        private int layoutResource;

        public void setContext(Context c) {
            this.context = c;
        }

        public ItemListAdapter(Context context, int layoutResource, List<OneItem> items) {
            super(context, layoutResource, items);
            this.context = context;
            this.items =  items;
            this.layoutResource = layoutResource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(layoutResource, null);
            }

            final OneItem oneItem = items.get(position);

            if (oneItem != null) {
                TextView content = (TextView) convertView.findViewById(R.id.content);
                TextView time = (TextView) convertView.findViewById(R.id.time);

                if (content != null){
                    content.setText(oneItem.content);
                }
                if (time != null){
                    time.setText(oneItem.time);
                }
            }
            return convertView;
        }
    }

    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        GPSListener gpsListener = new GPSListener();
        long minTime = 1000;
        float minDistance = 0;

        try {
            //최근에 알려진 위치 얻어오기
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                Timestamp cur = new Timestamp(System.currentTimeMillis());
                items.add(new OneItem(cur.toString(), cnt + "\n(" + latitude + "," + longitude+")"));
                cnt++;
                listView.setAdapter(adapter);
            }

            //주기적으로 GPS 정보 받도록 요청
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);

            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);
        }
        catch(SecurityException ex) {
            ex.printStackTrace();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            Timestamp cur = new Timestamp(System.currentTimeMillis());
            items.add(new OneItem(cur.toString(), cnt + "\n(" + latitude + "," + longitude+")"));
            cnt++;

            listView.setAdapter(adapter);
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}