package com.example.roadlink4;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RideDetails extends AppCompatActivity {

    private DatabaseReference ridesRef;
    private LinearLayout rideDetailsLayout;
    private EditText editTextCity;

    private static final int DELAY_TIME_MILLISECONDS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_details);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ridesRef = database.getReference("rides");


        editTextCity = findViewById(R.id.editTextCity);
        rideDetailsLayout = findViewById(R.id.rideDetailsLayout);

        Button btnGetRides = findViewById(R.id.btnGetRides);
        btnGetRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String enteredCity = editTextCity.getText().toString();
                getRidesWithEnteredCity(enteredCity);

                showGetRidesAnimation();
            }
        });
    }

    private void showGetRidesAnimation() {

        LottieAnimationView animationView = new LottieAnimationView(this);
        animationView.setAnimation(R.raw.wait);


        FrameLayout animationContainer = findViewById(R.id.animationContainer);
        animationContainer.addView(animationView);


        animationContainer.setBackgroundColor(getResources().getColor(android.R.color.white));


        animationContainer.setVisibility(View.VISIBLE);


        animationView.playAnimation();


        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                animationContainer.removeView(animationView);

                animationContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void getRidesWithEnteredCity(String enteredCity) {
        ridesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                rideDetailsLayout.removeAllViews();


                for (DataSnapshot rideSnapshot : snapshot.getChildren()) {
                    Ride ride = rideSnapshot.getValue(Ride.class);


                    if (ride != null && ride.getSourceLocation().contains(enteredCity)) {

                        displayRideDetails(ride);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e("RideDetails", "Error fetching rides", error.toException());
            }
        });
    }

    private void showCelebrationAnimation() {

        LottieAnimationView animationView = new LottieAnimationView(this);
        animationView.setAnimation(R.raw.celebration_animation);


        FrameLayout animationContainer = findViewById(R.id.animationContainer);
        animationContainer.addView(animationView);


        animationContainer.setBackgroundColor(getResources().getColor(android.R.color.white));


        animationContainer.setVisibility(View.VISIBLE);


        animationView.playAnimation();


        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {


                animationContainer.removeView(animationView);

                animationContainer.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void displayRideDetails(Ride ride) {

        LayoutInflater inflater = LayoutInflater.from(this);
        View rideCardView = inflater.inflate(R.layout.ride_cardview, null);


        TextView rideDetailsTextView = rideCardView.findViewById(R.id.rideDetailsTextView);
        TextView textViewDriverName = rideCardView.findViewById(R.id.textViewDriverName);
        TextView textViewPhoneNumber = rideCardView.findViewById(R.id.textViewPhoneNumber);
        TextView textViewSourceLocation = rideCardView.findViewById(R.id.textViewSourceLocation);
        TextView textViewDestinationLocation = rideCardView.findViewById(R.id.textViewDestinationLocation);
        TextView textViewDate = rideCardView.findViewById(R.id.textViewDate);
        TextView textViewTime = rideCardView.findViewById(R.id.textViewTime);
        Button btnBookRide = rideCardView.findViewById(R.id.btnBookRide);


        textViewDriverName.setText("" + ride.getUserName());
        textViewPhoneNumber.setText("" + ride.getUserPhoneNumber());
        textViewSourceLocation.setText("" + ride.getSourceLocation());
        textViewDestinationLocation.setText("" + ride.getDestinationLocation());
        textViewDate.setText("" + ride.getSelectedDate());
        textViewTime.setText("" + ride.getSelectedTime());


        btnBookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCelebrationAnimation();


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String currentUserName = currentUser.getDisplayName();


                    saveBookedRideToFirebase(ride, currentUserName);
                }


                delayedSnackbarMessage(v, "Ride Booked!");
            }
        });


        rideDetailsLayout.addView(rideCardView);
    }

    private void saveBookedRideToFirebase(Ride ride, String currentUserName) {

        DatabaseReference bookedRideRef = FirebaseDatabase.getInstance().getReference("bookedride");


        String bookedRideKey = bookedRideRef.push().getKey();


        bookedRideRef.child(bookedRideKey).setValue(ride);


        bookedRideRef.child(bookedRideKey).child("bookedBy").setValue(currentUserName);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserUid = currentUser.getUid();
            bookedRideRef.child(bookedRideKey).child("bookedByUid").setValue(currentUserUid);
        }
    }

    private void showSnackbarWithAnimation(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorAccent)); // Customize background color if needed
        snackbar.show();
    }

    private void delayedSnackbarMessage(View view, String message) {
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        showSnackbarWithAnimation(view, message);
                    }
                },
                DELAY_TIME_MILLISECONDS
        );
    }
}
