package com.polen.receipt.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.polen.receipt.R;
import com.polen.receipt.activities.MainActivity;
import com.polen.receipt.global.Global;

public class DetailMapFragment extends BaseFragment implements View.OnClickListener {

    MainActivity mContext;
    View mRootView;

    GoogleMap mGoogleMap = null;
    MapView mMapView;

    Double latitude;
    Double longitude;
    Bundle savedInstanceState;

    public static DetailMapFragment newInstance(){
        return new DetailMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = (MainActivity)getActivity();
        initView();
        return mRootView;
    }

    private void initView(){
        ImageView imgBack = mRootView.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        mMapView = (MapView) mRootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }


        latitude = Double.valueOf(Global.gReceiptInfo.latitude);
        longitude = Double.valueOf(Global.gReceiptInfo.longitude);

        Global.gCurrentLatitude = latitude;
        Global.gCurrentLongitude = longitude;

        refreshMap();
    }


    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void refreshMap(){
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;

                //zoom control enable
                mGoogleMap.getUiSettings().setZoomControlsEnabled(true);

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mGoogleMap.setMyLocationEnabled(true);

                try{
                    LatLng location = new LatLng(latitude, longitude);
                    mGoogleMap.addMarker(new MarkerOptions().position(location).title(latitude + " " + longitude).snippet(""));
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(12).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }catch (Exception e){}

                // For zooming automatically to the location of the marker

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(final LatLng latLng) {
                            changeLocation(latLng);
                    }
                });
            }
        });
    }


    private void changeLocation(final LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);


        // Clears the previously touched position
        mGoogleMap.clear();

        // Animating to the touched position
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mGoogleMap.addMarker(markerOptions);


//        AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
//        builder1.setMessage("Would you like to update well location? \n lat: " + latLng.latitude + "\n lon: " + latLng.longitude);
//        builder1.setCancelable(true);
//
//        builder1.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Global.gCurrentLatitude = latLng.latitude;
//                        Global.gCurrentLongitude = latLng.longitude;
//                        dialog.cancel();
////                        Utils.printDebug(mContext, Global.gCurrentLatitude + "Saved" + Global.gCurrentLongitude);
//                    }
//                });
//
//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//
//        AlertDialog alert11 = builder1.create();
//        alert11.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                mContext.onBackPressed();
                break;
        }
    }
}
