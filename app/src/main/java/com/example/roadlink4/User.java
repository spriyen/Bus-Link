package com.example.roadlink4;

public class User {

    private String name;
    private String age;
    private String phoneNumber;
    private String userId;  // Add this field for the user's unique identifier (UID)

    public User() {
        // Default constructor required for DataSnapshot.getValue(User.class)
    }

    public User(String name, String age, String phoneNumber, String userId) {
        this.name = name;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUserId() {
        return userId;
    }
}