package com.example.roadlink4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements MapsFragment.fragmentToActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        MapsFragment map = new MapsFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fargment_container,map);
        transaction.commit();
    }

    @Override
    public void sendDatatoPassenger(String data) {

    }

    @Override
    public void sendLatLng(LatLng latLng) {

    }

    @Override
    public void sendCurrentLocation(LatLng location) {

    }
}