package com.example.valery.bikemap;

import android.app.FragmentManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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

/**
 * Created by Valery on 02.05.2015.
 */
public class FragmentMap extends Fragment {

    public FragmentMap(){
        super();
    }
    GoogleMap myMap;
    Location myLocation;
    private UiSettings mUiSettings;

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View rootView = inflater.inflate(R.layout.fragment_map,container,false);
            setUpMapIfNeeded();
            return  rootView;
        }

        public void onResume() {
            super.onResume();
            setUpMapIfNeeded();
        }

        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.

            if (myMap == null) {
                Toast.makeText(getActivity(),
                        "Return map as null", Toast.LENGTH_SHORT).show();
                SupportMapFragment fragment = new SupportMapFragment();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.container1,fragment).commit();
                myMap = fragment.getMap();
                /*SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.add(R.id.container, mapFragment).commit();
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    public void onMapReady(GoogleMap googleMap) {
                        //GoogleMap myMap;
                        //setUpMap();
                    }
                });*/
                /*MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(FragmentMap.this);*/
                // getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
                // Try to obtain the map from the SupportMapFragment.
                /*myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMapAsync(this);*/
                // Check if we were successful in obtaining the map.
                if (myMap != null) {
                    Toast.makeText(getActivity(),
                            "Map is ready", Toast.LENGTH_SHORT).show();
                    setUpMap();
                }
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
}

