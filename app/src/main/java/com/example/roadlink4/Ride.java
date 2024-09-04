package com.example.roadlink4;

public class Ride {



    private String sourceLocation;
    private String destinationLocation;
    private String selectedDate;
    private String selectedTime;
    private String userName;
    private String userPhoneNumber;

    public Ride() {

    }

    public Ride(String sourceLocation, String destinationLocation, String selectedDate, String selectedTime, String userName, String userPhoneNumber) {
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.selectedDate = selectedDate;
        this.selectedTime = selectedTime;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getSourceLocation() {
        return sourceLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }
}