package com.example.valery.bikemap;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.Date;


/**
 * Created by Valery on 02.05.2015.
 */
public class FragmentMap extends Fragment {
    private LocationManager locationManager;
    public FragmentMap(){
        super();
    }
    GoogleMap myMap;
    PolylineOptions polylineOptions;
    LatLng startLatLng;
    //RoutesDataSource dataSource;
    private UiSettings mUiSettings;

    // Static LatLng
   /* LatLng startLatLng = new LatLng(30.707104, 76.690749);
    LatLng endLatLng = new LatLng(30.721419, 76.730017);*/

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
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            setUpMapIfNeeded();
            Button btnStart = (Button) rootView.findViewById(R.id.btnStart);
            Button btnStop = (Button) rootView.findViewById(R.id.btnStop);
            View.OnClickListener oclBtnStart = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    LatLng startLatLng = new LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(),
                           locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude());
                    drawRouteOnCreate();
                    Toast.makeText(getActivity().getBaseContext(), "Record is Started", Toast.LENGTH_SHORT).show();
                }
            };

            View.OnClickListener oclBtnStop = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    drawRouteOnCreate();
                    Toast.makeText(getActivity().getBaseContext(), "Record is Stopped", Toast.LENGTH_SHORT).show();
            }
            };
            btnStart.setOnClickListener(oclBtnStart);
            btnStop.setOnClickListener(oclBtnStop);
            return  rootView;
    }


    private void drawRouteOnCreate()
    {
        Polyline line = myMap.addPolyline(new PolylineOptions()
                .add(new LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(),
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude()),new LatLng(40.7, -74.0))
                        //.add(new LatLng(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude(), locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude()), new LatLng(40.7, -74.0))
                .width(5)
                .color(Color.RED));
    }

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
    private void drawMarker(LatLng point){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Adding marker on the Google Map
        myMap.addMarker(markerOptions);
    }

    private class LocationInsertTask extends AsyncTask<ContentValues, Void, Void>{
        @Override
        protected Void doInBackground(ContentValues... contentValues) {

            /** Setting up values to insert the clicked location into SQLite database */
            getActivity().getContentResolver().insert(LocationsContentProvider.CONTENT_URI, contentValues[0]);
            return null;
        }
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
                    //drawRouteOnCreate();
                    /*myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                        @Override
                        public void onMapClick(LatLng point) {
                            LocationsContentProvider locationsContentProvider = new LocationsContentProvider();
                            // Drawing marker on the map
                            drawMarker(point);

                            // Creating an instance of ContentValues
                            ContentValues contentValues = new ContentValues();

                            // Setting latitude in ContentValues
                            contentValues.put(LocationsDB.FIELD_LAT, point.latitude);

                            // Setting longitude in ContentValues
                            contentValues.put(LocationsDB.FIELD_LNG, point.longitude);

                            // Setting zoom in ContentValues
                            contentValues.put(LocationsDB.FIELD_ZOOM, myMap.getCameraPosition().zoom);

                            // Creating an instance of LocationInsertTask
                            LocationInsertTask insertTask = new LocationInsertTask();

                            // Storing the latitude, longitude and zoom level to SQLite database
                            insertTask.execute(contentValues);

                            Toast.makeText(getActivity().getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();
                        }
                    });*/
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

