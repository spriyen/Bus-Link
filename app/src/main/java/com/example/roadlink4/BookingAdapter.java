package com.example.roadlink4;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Ride> bookedRides;

    public void setBookedRides(List<Ride> bookedRides) {
        this.bookedRides = bookedRides;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ride bookedRide = bookedRides.get(position);


        holder.driverName.setText(" " + bookedRide.getUserName());
        holder.sourceLocation.setText("" + bookedRide.getSourceLocation());
        holder.destinationLocation.setText("" + bookedRide.getDestinationLocation());
        holder.date.setText("" + bookedRide.getSelectedDate());
        holder.time.setText("" + bookedRide.getSelectedTime());
    }

    @Override
    public int getItemCount() {
        return bookedRides != null ? bookedRides.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView driverName;
        TextView sourceLocation;
        TextView destinationLocation;
        TextView date;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driverName);
            sourceLocation = itemView.findViewById(R.id.sourceLocation);
            destinationLocation = itemView.findViewById(R.id.destinationLocation);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
        }
    }
}
