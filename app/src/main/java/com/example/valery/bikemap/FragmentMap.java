package com.example.valery.bikemap;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

/**
 * Created by Valery on 02.05.2015.
 */
public class FragmentMap extends Fragment {
    /*protected TextView tvlocation;
    protected TextView tvlocationNet;*/
    private LocationManager locationManager;
    public FragmentMap(){
        super();
    }
    GoogleMap myMap;
    Location myLocation;
    private UiSettings mUiSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initialize(
                Stetho.newInitializerBuilder(getActivity())
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(getActivity()))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(getActivity()))
                        .build());
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_map,container,false);
            //TextView tvlocation=(TextView)rootView.findViewById(R.id.tvlocation);
            //TextView tvlocationNet=(TextView)rootView.findViewById(R.id.tvlocationNet);
            //tvlocation.setText("Check");
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            setUpMapIfNeeded();
            return  rootView;

        }
    /*@Override
    public void onActivityCreated (Bundle savedInstanceState){

        TextView tvlocation=(TextView)getView().findViewById(R.id.tvlocation);
        TextView tvlocationNet=(TextView)getView().findViewById(R.id.tvlocationNet);
        super.onActivityCreated(savedInstanceState);
    }*/

    @Override
        public void onResume() {
            super.onResume();
            setUpMapIfNeeded();
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    locationListener);
            checkEnabled();
        }

    private void setUpMapIfNeeded() {

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentByTag("MAP_FRAGMENT_TAG");
        if (fragment ==null) {
            Toast.makeText(getActivity(), "Return map as null", Toast.LENGTH_SHORT).show();
            fragment = new SupportMapFragment();
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.add(R.id.map_container,fragment, "MAP_FRAGMENT_TAG").commit();
            fragment.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(GoogleMap googleMap) {
                    myMap = googleMap;
                    setUpMap();
                }
            });
        }
    }




        private void setUpMap() {
            myMap.setMyLocationEnabled(true);
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mUiSettings = myMap.getUiSettings();
            mUiSettings.setZoomControlsEnabled(true);
            mUiSettings.setMyLocationButtonEnabled(true);
            mUiSettings.setCompassEnabled(true);
            //myMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }


    @Override
    public void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        /*@Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {

            }
        }*/
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            TextView tvlocation=(TextView)getView().findViewById(R.id.tvlocation);

            tvlocation.setText(formatLocation(location));

        } else if (location.getProvider().equals(
                LocationManager.NETWORK_PROVIDER)) {
            TextView tvlocationNet=(TextView)getView().findViewById(R.id.tvlocationNet);
            tvlocationNet.setText(formatLocation(location));

        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format("Time positioning:\n"+"%3$tF %3$tT\n" + "Coordinates:\n"+"lat = %1$.4f\nlon = %2$.4f\n"+
                        "Speed:\n" + "%4$.1f m/c\n" + "%6$.1f km/h\n"+ "Accuracy:\n" + "%5$.1f m\n",

                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()), location.getSpeed(),location.getAccuracy(),location.getSpeed()*3.6);
    }
    private void checkEnabled() {

    }
}

