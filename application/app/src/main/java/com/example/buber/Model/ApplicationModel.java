package com.example.buber.Model;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Observable;

public class ApplicationModel extends Observable {
    private User sessionUser;
    private ArrayList<Trip> currentRequests;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public User getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(User sessionUser) {
        this.sessionUser = sessionUser;
        setChanged();
        notifyObservers();
    }

    public ArrayList<Trip> getCurrentRequests() {
        return currentRequests;
    }

    public void setCurrentRequests(ArrayList<Trip> currentRequests) {
        this.currentRequests = currentRequests;
        notifyObservers();
    }

}
