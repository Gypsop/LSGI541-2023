package com.LSGI541.lab.collection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GoogleMapsRouteQuery extends AppCompatActivity implements OnMapReadyCallback {

    // Declare the widget variables for connecting to the UI
    private Button widget_button_backToMain_googleMapsRouteQuery;
    private MapView widget_mapview_googleMapsRouteQuery;

    private EditText widget_edittext_googleMapsRouteQuery_from;
    private EditText widget_edittext_googleMapsRouteQuery_to;
    private Button widget_button_googleMapsRouteQuery_doQuery;
    private ProgressBar widget_progressbar_googleMapsRouteQuery;
    private Switch widget_switch_googleMapsRouteQuery_showDetails;
    private TextView widget_textview_googleMapsRouteQuery_queryStatus;
    private TextView widget_textview_googleMapsRouteQuery_routeDetails;
    private Spinner widget_spinner_googleMapsRouteQuery_language;

    String request_result_route_details = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_route_query);

        // Connect the MapView to the layout
        widget_mapview_googleMapsRouteQuery = findViewById(R.id.mapview_googleMapsRouteQuery);
        widget_mapview_googleMapsRouteQuery.onCreate(savedInstanceState);
        widget_mapview_googleMapsRouteQuery.getMapAsync((OnMapReadyCallback) this);

        // Connect the widgets related to query to the layout
        widget_edittext_googleMapsRouteQuery_from = findViewById(R.id.edittext_googleMapsRouteQuery_from);
        widget_edittext_googleMapsRouteQuery_to = findViewById(R.id.edittext_googleMapsRouteQuery_to);
        widget_button_googleMapsRouteQuery_doQuery = findViewById(R.id.button_googleMapsRouteQuery_doQuery);
        widget_progressbar_googleMapsRouteQuery = findViewById(R.id.progressbar_googleMapsRouteQuery);
        widget_switch_googleMapsRouteQuery_showDetails = findViewById(R.id.switch_googleMapsRouteQuery_showDetails);
        widget_textview_googleMapsRouteQuery_queryStatus = findViewById(R.id.textview_googleMapsRouteQuery_queryStatus);
        widget_textview_googleMapsRouteQuery_routeDetails = findViewById(R.id.textview_googleMapsRouteQuery_routeDetails);
        widget_spinner_googleMapsRouteQuery_language = findViewById(R.id.spinner_googleMapsRouteQuery_language);

        // Set up the button to go to the MainActivity activity
        widget_button_backToMain_googleMapsRouteQuery = findViewById(R.id.button_backToMain_googleMapsRouteQuery);
        widget_button_backToMain_googleMapsRouteQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoogleMapsRouteQuery.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set the default status
        widget_textview_googleMapsRouteQuery_routeDetails.setVisibility(View.GONE);
        widget_textview_googleMapsRouteQuery_routeDetails.setMovementMethod(ScrollingMovementMethod.getInstance()); // Enable scrolling
        widget_switch_googleMapsRouteQuery_showDetails.setChecked(false);
        widget_spinner_googleMapsRouteQuery_language.setSelection(3); // the 4th (index start from 0) choice - English, to be set as default here

        // Set up the switch to show/hide the route details
        widget_switch_googleMapsRouteQuery_showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (widget_switch_googleMapsRouteQuery_showDetails.isChecked()) {
                    widget_textview_googleMapsRouteQuery_routeDetails.setVisibility(View.VISIBLE);
                } else {
                    widget_textview_googleMapsRouteQuery_routeDetails.setVisibility(View.GONE);
                }
            }
        });

        // stop the progress bar
        widget_progressbar_googleMapsRouteQuery.setVisibility(View.GONE);

        // set the status
        widget_textview_googleMapsRouteQuery_queryStatus.setText(getString(R.string.textviewText_googleMapsRouteQuery_queryStatus_ready));

    }

    @Override
    protected void onResume() {
        super.onResume();
        widget_mapview_googleMapsRouteQuery.onResume();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        widget_button_googleMapsRouteQuery_doQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get query parameters from the UI
                String from = widget_edittext_googleMapsRouteQuery_from.getText().toString();
                String to = widget_edittext_googleMapsRouteQuery_to.getText().toString();

                if (from.isEmpty()) {
                    widget_edittext_googleMapsRouteQuery_from.setHintTextColor(Color.RED);
                    // set the status
                    widget_textview_googleMapsRouteQuery_queryStatus.setText(getString(R.string.textviewText_googleMapsRouteQuery_queryStatus_emptyInputWarning));

                    //return;
                }
                if (to.isEmpty()) {
                    widget_edittext_googleMapsRouteQuery_to.setHintTextColor(Color.RED);
                    // set the status
                    widget_textview_googleMapsRouteQuery_queryStatus.setText(getString(R.string.textviewText_googleMapsRouteQuery_queryStatus_emptyInputWarning));

                    //return;
                }
                if (!from.isEmpty() && !to.isEmpty()) {
                    // disable input to the edittexts
                    widget_edittext_googleMapsRouteQuery_from.setEnabled(false);
                    widget_edittext_googleMapsRouteQuery_to.setEnabled(false);

                    // disable the spinner for language selection
                    widget_spinner_googleMapsRouteQuery_language.setEnabled(false);

                    // start the progress bar
                    widget_progressbar_googleMapsRouteQuery.setVisibility(View.VISIBLE);
                    // disable the button
                    widget_button_googleMapsRouteQuery_doQuery.setEnabled(false);
                    // set the status
                    widget_textview_googleMapsRouteQuery_queryStatus.setText(" ");

                    // set the hint colors back (in case they were set red before)
                    widget_edittext_googleMapsRouteQuery_from.setHintTextColor(widget_edittext_googleMapsRouteQuery_from.getHintTextColors().getDefaultColor());
                    widget_edittext_googleMapsRouteQuery_to.setHintTextColor(widget_edittext_googleMapsRouteQuery_to.getHintTextColors().getDefaultColor());

                    // build the URL
                    String from_string = widget_edittext_googleMapsRouteQuery_from.getText().toString().replace(" ", "+");
                    String to_string = widget_edittext_googleMapsRouteQuery_to.getText().toString().replace(" ", "+");
                    String language_string = "en"; // Defaultly set to English
                    switch((int)widget_spinner_googleMapsRouteQuery_language.getSelectedItemId()) {
                        case 0:
                            language_string = "zh-CN";
                            break;
                        case 1:
                            language_string = "zh-HK";
                            break;
                        case 2:
                            language_string = "zh-TW";
                            break;
                        case 3:
                            language_string = "en";
                            break;
                        case 4:
                            language_string = "ja";
                            break;
                    }
                    String query_url = getString(R.string.google_maps_queryUrl_section1) + from_string
                                     + getString(R.string.google_maps_queryUrl_section2) + to_string
                                     + getString(R.string.google_maps_queryUrl_section3) + getString(R.string.google_maps_key)
                                     + getString(R.string.google_maps_queryUrl_section4) + language_string;

                    // Create a new Thread to run the network request
                    // (because we don't want the time-consuming request blocking the UI thread, making the APP unresponsive)
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // Network request
                            try {
                                //Requests the server
                                URL request_url = new URL(query_url);
                                HttpURLConnection urlConnection = (HttpURLConnection) request_url.openConnection();
                                urlConnection.setRequestMethod("GET");
                                urlConnection.setConnectTimeout(8000);
                                urlConnection.setReadTimeout(8000);
                                urlConnection.setDoInput(true);
                                urlConnection.setDoOutput(true);
                                urlConnection.connect();
                                InputStream in = urlConnection.getInputStream();
                                // Read the response
                                Reader reader = new InputStreamReader(in);
                                BufferedReader bufferedReader = new BufferedReader(reader);
                                String line = "";
                                StringBuilder stringBuilder = new StringBuilder();
                                while ((line = bufferedReader.readLine()) != null) {
                                    stringBuilder.append(line);
                                }
                                // Close all streams
                                bufferedReader.close();
                                reader.close();
                                in.close();
                                // Close connection
                                urlConnection.disconnect();
                                // Save the request result
                                String request_result = stringBuilder.toString();
                                // Show the request result (if you want)
                                /*
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(request_result);
                                    }
                                });
                                */

                                // Parse the request result
                                JSONObject request_result_json = new JSONObject(request_result);

                                String request_result_polyline_points = request_result_json.getJSONArray("routes").getJSONObject(0).getJSONObject("overview_polyline").get("points").toString();


                                // Prepare the route details
                                for (int i = 0; i < request_result_json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").length(); i++) {
                                    request_result_route_details += request_result_json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).get("html_instructions").toString();
                                    if (i != request_result_json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").length() - 1) {
                                        request_result_route_details += "<br>";
                                    }
                                }


                                // decode the polyline points
                                java.util.List<LatLng> decoded_points = PolyUtil.decode(request_result_polyline_points);

                                // show the decoded points
                                PolylineOptions polylineOptions = new PolylineOptions();
                                polylineOptions.addAll(decoded_points);
                                polylineOptions.color(Color.RED);
                                polylineOptions.jointType(JointType.ROUND);
                                polylineOptions.width(15f);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        map.addPolyline(polylineOptions);

                                        widget_textview_googleMapsRouteQuery_routeDetails.setText(Html.fromHtml(request_result_route_details));

                                        LatLng viewPosition = new LatLng(22.305, 114.1795); //PolyU coordinate in WGS84
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(viewPosition, 12));
                                    }
                                });

                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (ProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // stop the progress bar
                                    widget_progressbar_googleMapsRouteQuery.setVisibility(View.GONE);
                                    // set the status
                                    widget_textview_googleMapsRouteQuery_queryStatus.setText(getString(R.string.textviewText_googleMapsRouteQuery_queryStatus_finished));


                                }
                            });


                        }
                    });
                    thread.start();

                }

            }
        });

    }

    @Override
    protected void onPause() {
        // this function is called when the activity is paused (user switches to another activity)

        /*
        widget_mapview_googleMapsRouteQuery.onPause();
        super.onPause();
        */

        // make sure that every time user open this activity can get a totally new one
        // (destroy it when the activity is going to be paused)
        this.finish();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        widget_mapview_googleMapsRouteQuery.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        widget_mapview_googleMapsRouteQuery.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        widget_mapview_googleMapsRouteQuery.onSaveInstanceState(outState);
    }
}