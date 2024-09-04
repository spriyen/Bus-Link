package com.example.roadlink4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class Passengerhome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passengerhome2);


        Button btnNewYork = findViewById(R.id.btnNewYork);


        btnNewYork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start ShowRideDetailsActivity and pass the selected city
                Intent intent = new Intent(Passengerhome.this, RideDetails.class);
                intent.putExtra("selectedCity", "Tamil Nadu");
                startActivity(intent);
            }
        });
    }
}