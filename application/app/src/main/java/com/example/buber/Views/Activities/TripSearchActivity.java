package com.example.buber.Views.Activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.buber.App;
import com.example.buber.Controllers.ApplicationController;
import com.example.buber.Model.ApplicationModel;
import com.example.buber.Model.Trip;
import com.example.buber.Model.User;
import com.example.buber.R;
import com.example.buber.Views.Components.CustomTripList;
import com.example.buber.Views.Components.TripSearchRecord;
import com.example.buber.Views.Fragments.AcceptTripRequestFragment;
import com.example.buber.Views.UIErrorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Main Driver activity for searching for and selecting a trip. Activity loads trips and uses
 * the model to display a list of trips that are filtered based on the users current radius.
 * TODO: MVC Updating and Error Handling.
 */
public class TripSearchActivity extends AppCompatActivity implements UIErrorHandler, Observer,
        AcceptTripRequestFragment.OnFragmentInteractionListener {

    ListView tripSearchList;
    ArrayAdapter<TripSearchRecord> tripSearchRecordArrayAdapter;
    ArrayList<TripSearchRecord> tripDataList;

    private static final String TAG = "TripSearchActivity";

    /**onCreate method creates the view. It is used to populate TripSearchActivity
     * @param savedInstanceState calls the previous saved state if there is one*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_search_activity);
        tripSearchList = findViewById(R.id.trip_search_list);
        App.getModel().addObserver(this);

        tripDataList = new ArrayList<>();

        //Activate the custom array adapter (CustomTripList)
        tripSearchRecordArrayAdapter = new CustomTripList(this, tripDataList);
        tripSearchList.setAdapter(tripSearchRecordArrayAdapter);

        tripSearchList.setOnItemClickListener((parent, view, position, id) ->
                new AcceptTripRequestFragment(tripDataList.get(position),
                        position,
                        TripSearchActivity.this).show(getSupportFragmentManager(),
                        "VIEW_RECORD"));
        updateTripList();
    }

    /**updateTripList updates the trips but getting trips for the User*/
    public void updateTripList() {
        ApplicationController.getTripsForUser(this);
    }

    /**onError function is used to handle incoming UI errors in tripSearchActivity
     * @param e is the error called*/
    @Override
    public void onError(Error e) {
        // TODO: Handle Incoming UI Errors
    }

    /**Update function updates the view whenever changes are made
     * @param o,arg are used to ensure the correct updates are made*/
    @Override
    public void update(Observable o, Object arg) {
        ApplicationModel m = (ApplicationModel) o;
        User sessionUser = m.getSessionUser();
        List<Trip> tripList = m.getSessionTripList();
        if (tripList != null) {
            tripDataList.clear();
            for (Trip t : tripList) {
                tripDataList.add(new TripSearchRecord(t, sessionUser.getCurrentUserLocation()));
            }
            tripSearchRecordArrayAdapter.notifyDataSetChanged();
        }
    }

    /**onAcceptPressed handles user interaction when the accept button is pressed
     * @param tripSearchRecord,position uses the position and trip search record to ensure the correct
     *      trip is accepted*/
    public void onAcceptPressed(TripSearchRecord tripSearchRecord, int position){
        Trip selectedTrip = App.getModel().getSessionTripList().get(position);
        ApplicationController.handleDriverTripSelect(selectedTrip);
        // Navigate back to main activity
        this.finish();
    }

    /**
     * OnDestroy handles activity when application is closed down*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        ApplicationModel m = App.getModel();
        m.deleteObserver(this);
    }
}
