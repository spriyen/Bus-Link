package com.example.roadlink4;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBooking extends AppCompatActivity {

    private DatabaseReference bookedRideRef;
    private RecyclerView recyclerViewBookings;
    private TextView textNoBookings;
    private BookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        bookedRideRef = database.getReference("bookedride");


        recyclerViewBookings = findViewById(R.id.recyclerViewBookings);
        textNoBookings = findViewById(R.id.textNoBookings);


        recyclerViewBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingAdapter = new BookingAdapter();
        recyclerViewBookings.setAdapter(bookingAdapter);


        fetchAndDisplayBookedRides();
    }

    private void fetchAndDisplayBookedRides() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            bookedRideRef.orderByChild("bookedByUid").equalTo(currentUserId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<Ride> bookedRides = new ArrayList<>();

                            for (DataSnapshot bookingSnapshot : snapshot.getChildren()) {
                                Ride bookedRide = bookingSnapshot.getValue(Ride.class);
                                if (bookedRide != null) {
                                    bookedRides.add(bookedRide);
                                }
                            }


                            displayBookedRides(bookedRides);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void displayBookedRides(List<Ride> bookedRides) {
        if (bookedRides.isEmpty()) {

            textNoBookings.setVisibility(TextView.VISIBLE);
            recyclerViewBookings.setVisibility(RecyclerView.GONE);
        } else {

            textNoBookings.setVisibility(TextView.GONE);
            recyclerViewBookings.setVisibility(RecyclerView.VISIBLE);

            bookingAdapter.setBookedRides(bookedRides);
        }
    }
}
