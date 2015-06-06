package uk.ac.ox.ibme.cwm_android_06;


import android.app.FragmentManager;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;
import android.widget.Button;
import android.content.Intent;
import android.widget.SeekBar;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.TextView;
import android.app.AlarmManager;
import android.location.Criteria;
import android.app.PendingIntent;
import android.location.Location;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.preference.PreferenceManager;


//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxCallback;
//import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

//import com.google.android.gms.
import com.google.android.gms.location.places.AddPlaceRequest;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View.OnClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.PolylineOptions;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;

import java.net.URI;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

public class MainActivity extends FragmentActivity
    implements ConnectionCallbacks,OnConnectionFailedListener {

    private Button suggest = null;
    private Button draw = null;
    private SharedPreferences SP = null;
    private boolean locationAvailable = false;
    private LocationManager locationManager = null;
    private int position_index;
    private Location lastKnownLocation;
    ArrayList<LatLng> markerPoints;
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    //private IntentFilter myFilter = new IntentFilter(ACTION_NAME);

    // AQuery aq;

    // The definition of the ALARM ID and
    // the creation of a Filter that will
    // enable us to act only on this specific
    // alarm signal
    public static final String ACTION_NAME = "uk.ac.ox.ibme.android_06.MYALARM";
    PendingIntent pendingIntent = null;

    // Polyline variables
    PolylineOptions polylineOptions = new PolylineOptions();
    Polyline polyline = null;

    // A variable that will contains the information about
    // the Map we are receiving from the Google API
    private GoogleMap mMap;

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {

            SupportMapFragment smf = null;
            smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mMap = smf.getMap();

            // This method initialise the blue dot that show our current location on the map
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    // Define a listener that responds to location updates. The updates arrives
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Store location data every time the location changes
            lastKnownLocation = location;

            if (locationAvailable && lastKnownLocation != null) {
                Log.d(this.toString(), "geo: " + lastKnownLocation.getLongitude() + "," + lastKnownLocation.getLatitude() + "," + lastKnownLocation.getAltitude());
            }

            // Check if the mMap is initialised
            if (mMap != null && lastKnownLocation != null) {
                LatLng position = null;
                position = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                MarkerOptions mo = null;
                mo = new MarkerOptions().title(Integer.toString(position_index)).position(position);

                if (mo != null) {
                    position_index = position_index + 1;
                }
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };


    // This is the function that returns the name of the best location provider given a certain set of criteria
    private String getBestLocationProvider() {
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        // Here return the output value of the getBestProvider method
        return locationManager.getBestProvider(c, true);
    }


    //This method activates the LocationManager
    private void activateLocationManager() {

        position_index = 0;

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        // Here we select the best provider of location available that fit some criteria and is available.
        String bestProvider = getBestLocationProvider();

        // We set this flag to false and later we check the result to understand if the location is
        // available
        locationAvailable = false;


        // If the bestProvider is not accessible and/or enabled
        if (!locationManager.isProviderEnabled(bestProvider)) {
            // .. we create a custom AlertDialog (as seen in Module 3) that will
            // initiate the Intent to activate the Location management for the phone
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your LOCATION provider seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }


        // In case the location manager and the bestProvider are not empty and previous checks succeeded...
        if (locationManager != null && bestProvider != "") {
            locationManager.requestLocationUpdates(bestProvider, 3000, 5, locationListener);
            lastKnownLocation = locationManager.getLastKnownLocation(bestProvider);
            locationAvailable = true;
        }

    }

    // We make sure to clean up the alert before leaving...
    // We do not want to keep any resource going after our app is gone.
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        aq = new AQuery(this);
//        aq.ajax( "http://echo.jsontest.com/insert-key-here/insert-value-here/key/value_android_CWM", JSONObject.class, new AjaxCallback<JSONObject>() {
//
//            @Override
//            public void callback(String url, JSONObject json, AjaxStatus status) {
//                if(json != null){
//                    //successful ajax call, show status code and json content
//                    try {
//                        Toast.makeText(aq.getContext(), json.getString("key") , Toast.LENGTH_LONG).show();
//
//
//                        ((JSONObject) json.getJSONArray("ace").get(0)).getDouble("")
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }else{
//
//                    //ajax error, show error code
//                    Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        // This function initialise the MAP object
        setUpMapIfNeeded();
        activateLocationManager();

        // The SharedPreference Manager to store
        // information we might need after when
        // the application is restarted
        SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mGoogleApiClient = new GoogleApiClient
                .Builder( this )
                .enableAutoManage(this, 0, this)
                .addApi( Places.GEO_DATA_API )
                .addApi( Places.PLACE_DETECTION_API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();


        // Initializing
        // markerPoints is an array, sth like [lat/lng: (51.75060887692055,-1.257004514336586), lat/lng: (51.75513937028631,-1.2594044208526611), lat/lng: (51.753900731883256,-1.2564617022871971), lat/lng: (51.75317513301807,-1.260518878698349)]

        // TODO ON FRIDAY MORNING
        int size = SP.getInt("Points_size", -1);
       // markerPoints.clear();
        markerPoints = new ArrayList<LatLng>();

        MarkerOptions options2 = new MarkerOptions();

        if (size != -1) {
            for (int iii = 0; iii < size; iii++) {
                Double lat = Double.parseDouble(SP.getString("Points_" + iii + "0", ""));
                Double lng = Double.parseDouble(SP.getString("Points_" + iii + "1", ""));
                LatLng position = new LatLng(lat, lng);
                markerPoints.add(position);

                options2.position(position);
                if (markerPoints.size() == 1) {
                    options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }
                mMap.addMarker(options2);
            }

            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }


        //if (null == markerPoints) {
            markerPoints = new ArrayList<LatLng>(); // TODO: SET ARGUMENT AS SP.GET WHATEVER
        //} else {

        //}



        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        mMap = fm.getMap();
        // Enable MyLocation Button in the Map
        mMap.setMyLocationEnabled(true);
        // Setting onclick event listener for the map
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {

                // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
                // Up to 8 waypoints are allowed in a query for non-business users
                if (markerPoints.size() >= 10) {
                    return;
                }
                // Adding new item to the ArrayList
                markerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED and
                 * for the rest of markers, the color is AZURE
                 */
                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                } else {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

            }



        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker pointer) {

                LatLng loc = pointer.getPosition();
                Uri gmmIntentUri = Uri.parse("google.streetview:cbll=" + Double.toString(loc.latitude) + "," + Double.toString(loc.longitude));


                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                return true;
            }
        });
        suggest = (Button) findViewById(R.id.suggest);
        // Assign the ClickListener that will manage the 'click' event
        suggest.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if( mGoogleApiClient == null || !mGoogleApiClient.isConnected() )
//                            return;

                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                        try {
                            startActivityForResult( builder.build(MainActivity.this), PLACE_PICKER_REQUEST );
                        } catch ( GooglePlayServicesRepairableException e ) {
                            Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
                        } catch ( GooglePlayServicesNotAvailableException e ) {
                            Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
                        }

                    }
                });

        draw = (Button) findViewById(R.id.draw);
        // Click event handler for Button btn_draw
        draw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(origin, dest);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);

                    // TODO: SAVE IMMEDIATELY TO SP ---------------------------------------------------->

                    System.out.println(markerPoints);

                    //Save immediately in SP
                    SP.edit().putInt("Points_size", markerPoints.size()).commit();
                    // markerPoints is an array, sth like [lat/lng: (51.75060887692055,-1.257004514336586), lat/lng: (51.75513937028631,-1.2594044208526611), lat/lng: (51.753900731883256,-1.2564617022871971), lat/lng: (51.75317513301807,-1.260518878698349)]
                    for (int iii=0; iii<markerPoints.size(); iii++) {

                        SP.edit().remove("Points_" + iii + "0").commit();
                        SP.edit().remove("Points_" + iii + "1").commit();

                        SP.edit().putString("Points_" + iii + "0", Double.toString(markerPoints.get(iii).latitude)).commit();
                        SP.edit().putString("Points_" + iii + "1", Double.toString(markerPoints.get(iii).longitude)).commit();

                    }
                }

            }
        });

        // The map will be cleared on long click
        mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng point) {
                // Removes all the points from Google Map
                mMap.clear();
                // Removes all the points in the ArrayList
                markerPoints.clear();
                SP.edit().clear().commit();
            }
        });
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Sensor enabled
        String sensor = "sensor=false";
        // Waypoints
        String waypoints = "";
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }
        String mode = "mode=walking";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode + "&"+ waypoints;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("", "Exception while downloading url");
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

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
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.


    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        //More about this in the 'Handle Connection Failures' section.

    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            //displayPlace( PlacePicker.getPlace( data, this ) );
            Place place = PlacePicker.getPlace(data, this);
            LatLng point = place.getLatLng();
            // Already 10 locations with 8 waypoints and 1 start location and 1 end location.
            // Up to 8 waypoints are allowed in a query for non-business users
            if (markerPoints.size() >= 10) {
                return;
            }
            // Adding new item to the ArrayList
            markerPoints.add(point);

            // Creating MarkerOptions
            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(point);

            /**
             * For the start location, the color of marker is GREEN and
             * for the end location, the color of marker is RED and
             * for the rest of markers, the color is AZURE
             */
            if (markerPoints.size() == 1) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else if (markerPoints.size() == 2) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            } else {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            }

            // Add new marker to the Google Map Android API V2
            mMap.addMarker(options);

        }
    }
};
