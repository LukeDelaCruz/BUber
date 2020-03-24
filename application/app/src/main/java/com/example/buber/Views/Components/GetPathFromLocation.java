package com.example.buber.Views.Components;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.example.buber.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * GetPathFromLocation class makes an api call to DirectionsAPI. It creates a URL using the start
 * and end locations of the rider trip. Then it parses the route data into the polylines used
 * Source Citation: https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
 * Function calls Direction API and adds route polyline to map
 * to create a driving route on the map*/
//Source Citation: https://stackoverflow.com/questions/47492459/how-do-i-draw-a-route-along-an-existing-road-between-two-points
public class GetPathFromLocation extends AsyncTask<String, Void, PolylineOptions> {
    private String TAG = "GetPathFromLocation";
    //Private String of APIKEY
    private String APIKEY = "AIzaSyDFEIMmFpPoMijm_0YraJn4S33UvtlnqF8";

    private com.google.android.gms.maps.model.LatLng source, destination;
    private DirectionPointListener resultCallback;

    /**Constructor for GetPathFromLocation
     * @param source a LatLng variable of the start location
     * @param destination a LatLng variable of the end location
     * @param  resultCallback a DirectionPointListener used for the path call*/
    public GetPathFromLocation(LatLng source,
                               LatLng destination,
                               DirectionPointListener resultCallback){
        this.source = source;
        this.destination = destination;
        this.resultCallback = resultCallback;
    }

    /**GetURL() returns a URL string for use in the API call
     * @param  origin a LatLng variable of the StartLocation
     * @param  destination a LatLng variable of the EndLocation*/
    public String getUrl(LatLng origin, LatLng destination){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + "," + destination.longitude;
        //sensor may not be necessary
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + APIKEY;

        return url;
    }

    /**doInBackground
     * @param url is the url used to make an api call for DirectionsAPI*/
    @Override
    protected PolylineOptions doInBackground(String... url) {
        String data;

        try {
            InputStream inputStream = null;
            HttpURLConnection connection = null;
            try {
                URL directionUrl = new URL(getUrl(source, destination));
                connection = (HttpURLConnection) directionUrl.openConnection();
                connection.connect();
                inputStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer stringBuffer = new StringBuffer();

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }

                data = stringBuffer.toString();
                bufferedReader.close();

            } catch (Exception e) {
                Log.e(TAG, "Exception : " + e.toString());
                return null;
            } finally {
                inputStream.close();
                connection.disconnect();
            }
            Log.e(TAG, "Background Task data : " + data);


            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(data);
                // Starts parsing data
                DirectionHelper helper = new DirectionHelper();
                routes = helper.parse(jsonObject);
                Log.e(TAG, "Executing Routes : "/*, routes.toString()*/);


                ArrayList<LatLng> points;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = routes.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.RED);

                    Log.e(TAG, "PolylineOptions Decoded");
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    return lineOptions;
                } else {
                    return null;
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception in Executing Routes : " + e.toString());
                return null;
            }

        } catch (Exception e) {
            Log.e(TAG, "Background Task Exception : " + e.toString());
            return null;
        }
    }

    /**onPostExecute executes the drawing of the polyline
     * @param polylineOptions the instance of the polyline*/
    @Override
    protected void onPostExecute(PolylineOptions polylineOptions) {
        super.onPostExecute(polylineOptions);
        if (resultCallback != null && polylineOptions != null)
            resultCallback.onPath(polylineOptions);
    }
}


