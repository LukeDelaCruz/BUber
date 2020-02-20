package com.example.buber.Views.Activities.Models;
import androidx.annotation.NonNull;
/**
 * A simple Rider class that holds the Rider properties and is the link to Fb "Rider" collection
 */
public class Rider {

    private  String firstName;
    private  String lastName;
    private  String userID;

    public Rider(){}        // VERY IMPORTANT TO HAVE


    public Rider(String firstName, String lastName, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userID = userID;
    }

    public String getFirstName() {
        return firstName; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @NonNull
    @Override
    public String toString() {
        return "First Name: " + firstName + " Last Name: " + lastName + " User ID: " +  userID;
    }


}