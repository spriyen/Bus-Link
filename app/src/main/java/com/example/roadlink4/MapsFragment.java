package com.example.roadlink4;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ValueEventListener;


public class MapsFragment extends Fragment implements GoogleMap.OnMapClickListener {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private GoogleMap mMap;
    private Marker currentMarker;

    private EditText sloc;
    private EditText dloc;
    private LatLng startPoint, endPoint;
    boolean flag = false;
    int cursor = 0;

    private SearchView searchView;

    private Button btnSelectDate, btnSelectTime, btnOfferRide;
    private Calendar selectedDateTime = Calendar.getInstance();


    private DatabaseReference databaseReference;

    public interface fragmentToActivity {
        void sendDatatoPassenger(String data);

        void sendLatLng(LatLng latLng);

        void sendCurrentLocation(LatLng location);
    }

    fragmentToActivity callBack;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callBack = (fragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement fragmentToActivity");
        }
    }

    private final OnMapReadyCallback callback = googleMap -> {
        mMap = googleMap;
        if (flag) {
            mMap.addMarker(new MarkerOptions().position(startPoint).title("Starting Point"));
            mMap.addMarker(new MarkerOptions().position(endPoint).title("Ending Point"));
            PolylineOptions polylineOptions = new PolylineOptions().add(startPoint, endPoint).width(12).color(Color.GRAY);
            mMap.addPolyline(polylineOptions);
        }
        if (checkLocationPermission()) {
            showCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
        mMap.setOnMapClickListener(flag ? null : this);  // Set map click listener
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_support, container, false);


        databaseReference = FirebaseDatabase.getInstance().getReference("rides");

        sloc = view.findViewById(R.id.sloc);
        dloc = view.findViewById(R.id.dloc);
        searchView = view.findViewById(R.id.searchView);

        sloc.setOnClickListener(v -> cursor = 0);
        dloc.setOnClickListener(v -> cursor = 1);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        btnSelectDate = view.findViewById(R.id.btnSelectDate);
        btnSelectTime = view.findViewById(R.id.btnSelectTime);
        btnOfferRide = view.findViewById(R.id.btnOfferRide);

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());
        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());
        btnOfferRide.setOnClickListener(v -> offerRide());

        return view;
    }

    private void searchLocation(String locationName) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng searchLocation = new LatLng(address.getLatitude(), address.getLongitude());

                if (currentMarker != null) {
                    currentMarker.remove();
                }

                currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(searchLocation)
                        .title("Search Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation, 15));

                if (cursor == 0) {
                    sloc.setText(address.getAddressLine(0));
                    callBack.sendLatLng(searchLocation);
                    callBack.sendDatatoPassenger(address.getAddressLine(0));
                } else if (cursor == 1) {
                    dloc.setText(address.getAddressLine(0));
                    callBack.sendLatLng(searchLocation);
                    callBack.sendDatatoPassenger(address.getAddressLine(0));
                }

            } else {
                Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && mMap != null) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(requireContext(), "Please enable location services", Toast.LENGTH_LONG).show();
                    return;
                }
                mMap.setMyLocationEnabled(true);
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                callBack.sendCurrentLocation(currentLocation);
                                mMap.setMinZoomPreference(9);
                                mMap.setMaxZoomPreference(18);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                            } else {
                                Toast.makeText(requireContext(), "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(requireActivity(), e -> {
                            Toast.makeText(requireContext(), "Unable to fetch current location", Toast.LENGTH_SHORT).show();
                        });
            }
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, monthOfYear);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String selectedDate = sdf.format(selectedDateTime.getTime());
                    btnSelectDate.setText(selectedDate);
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    String selectedTime = sdf.format(selectedDateTime.getTime());
                    btnSelectTime.setText(selectedTime);
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                true);

        timePickerDialog.show();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
        }

        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Custom Marker")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        getAddressFromLatLng(latLng);
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            callBack.sendLatLng(latLng);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                if (cursor == 1) {
                    dloc.setText(addressText);
                    cursor = 0;
                } else {
                    sloc.setText(addressText);
                    cursor = 1;
                }
                callBack.sendDatatoPassenger(addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void offerRide() {
        String sourceLocation = sloc.getText().toString().trim();
        String destinationLocation = dloc.getText().toString().trim();
        String selectedDate = btnSelectDate.getText().toString().trim();
        String selectedTime = btnSelectTime.getText().toString().trim();

        // Retrieve user information from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String userName = user.getName();
                            String userPhoneNumber = user.getPhoneNumber();

                            // Now, userName and userPhoneNumber are available, you can use them
                            Ride ride = new Ride(sourceLocation, destinationLocation, selectedDate, selectedTime, userName, userPhoneNumber);

                            // Store ride details in Firebase Realtime Database
                            String rideId = FirebaseDatabase.getInstance().getReference("rides").push().getKey();
                            if (rideId != null) {
                                FirebaseDatabase.getInstance().getReference("rides").child(rideId).setValue(ride);
                                Toast.makeText(requireContext(), "Ride offered successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to offer ride", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors here
                }
            });
        } else {

            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDetach() {
        callBack = null;
        super.onDetach();
    }
}
