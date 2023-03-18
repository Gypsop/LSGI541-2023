package com.LSGI541.lab.collection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.icu.text.SimpleDateFormat;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MangkhutPath extends AppCompatActivity {

    // Declare the widget variables for connecting to the UI
    private Button widget_button_backToMain_mangkhutPath;
    private MapView widget_mapview_mangkhutPath;

    private Callout callout_mangkhutPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mangkhut_path);

        // ArcGIS Runtime API key
        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcgis_allinone_api_key));

        Portal portal = new Portal("https://www.arcgis.com", false);

        // Connect the MapView to the layout
        widget_mapview_mangkhutPath = findViewById(R.id.mapview_mangkhutPath);

        // Prepare layers
        ServiceFeatureTable serviceFeatureTable_mangkhutPath = new ServiceFeatureTable(getString(R.string.mangkhutPath_styledFeatureLayer_url));
        FeatureLayer featureLayer_mangkhutPath_fromUrl = new FeatureLayer(serviceFeatureTable_mangkhutPath);

        PortalItem portalItem_mangkhutPath = new PortalItem(portal, getString(R.string.mangkhutPath_styledFeatureLayer_id));
        FeatureLayer featureLayer_mangkhutPath_fromPortal = new FeatureLayer(portalItem_mangkhutPath);

        // Prepare the map for showing both heatmap and callout
        ArcGISMap heatmapAndCalloutMap = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);
        // Must be the heatmap layer added AFTER adding the callout layer
        // To make the heatmap layer on TOP of the callout layer
        heatmapAndCalloutMap.getOperationalLayers().add(featureLayer_mangkhutPath_fromUrl); // To support the callout
        heatmapAndCalloutMap.getOperationalLayers().add(featureLayer_mangkhutPath_fromPortal); // To support the heatmap

        // Set the map to the MapView
        widget_mapview_mangkhutPath.setMap(heatmapAndCalloutMap);
        widget_mapview_mangkhutPath.setViewpoint(new Viewpoint(22.305, 114.1795, 50000000)); //PolyU coordinate in WGS84, scale 50000000

        // Set up the callout
        callout_mangkhutPath = widget_mapview_mangkhutPath.getCallout();

        widget_mapview_mangkhutPath.setOnTouchListener(new DefaultMapViewOnTouchListener(this, widget_mapview_mangkhutPath) {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // remove any existing callouts
                if (callout_mangkhutPath.isShowing()) {
                    callout_mangkhutPath.dismiss();
                }
                // get the point that was clicked and convert it to a point in map coordinates
                final Point screenPoint = new Point(Math.round(e.getX()), Math.round(e.getY()));
                // create a selection tolerance
                int tolerance = 10;
                // use identifyLayerAsync to get tapped features
                final ListenableFuture<IdentifyLayerResult> identifyLayerResultListenableFuture = widget_mapview_mangkhutPath
                        .identifyLayerAsync(featureLayer_mangkhutPath_fromUrl, screenPoint, tolerance, false, 1);
                identifyLayerResultListenableFuture.addDoneListener(() -> {
                    try {
                        IdentifyLayerResult identifyLayerResult = identifyLayerResultListenableFuture.get();
                        // create a textview to display field values
                        TextView calloutContent = new TextView(getApplicationContext());
                        calloutContent.setTextColor(Color.BLACK);
                        calloutContent.setSingleLine(false);
                        calloutContent.setVerticalScrollBarEnabled(true);
                        calloutContent.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
                        calloutContent.setMovementMethod(new ScrollingMovementMethod());
                        calloutContent.setLines(7);
                        for (GeoElement element : identifyLayerResult.getElements()) {
                            Feature feature = (Feature) element;
                            // create a map of all available attributes as name value pairs
                            Map<String, Object> attr = feature.getAttributes();
                            Set<String> keys = attr.keySet();
                            for (String key : keys) {
                                Object value = attr.get(key);
                                // format observed field value as date
                                if (value instanceof GregorianCalendar) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                    value = simpleDateFormat.format(((GregorianCalendar) value).getTime());
                                }
                                // append name value pairs to text view
                                calloutContent.append(key + " | " + value + "\n");
                            }
                            // center the mapview on selected feature
                            Envelope envelope = feature.getGeometry().getExtent();
                            mMapView.setViewpointGeometryAsync(envelope, 200);
                            // show callout
                            callout_mangkhutPath.setLocation(envelope.getCenter());
                            callout_mangkhutPath.setContent(calloutContent);
                            callout_mangkhutPath.show();
                        }
                    } catch (Exception e1) {
                        Log.e(getResources().getString(R.string.app_name), "Select feature failed: " + e1.getMessage());
                    }
                });
                return super.onSingleTapConfirmed(e);
            }
        });


        // Set up the button to go to the MainActivity activity
        widget_button_backToMain_mangkhutPath = findViewById(R.id.button_backToMain_mangkhutPath);
        widget_button_backToMain_mangkhutPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MangkhutPath.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    // Pause the MapView when the activity is paused
    @Override
    protected void onPause() {
        widget_mapview_mangkhutPath.pause();
        super.onPause();
    }

    // Resume the MapView when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        widget_mapview_mangkhutPath.resume();
    }

    // Release the MapView resources when the activity is destroyed
    @Override
    protected void onDestroy() {
        widget_mapview_mangkhutPath.dispose();
        super.onDestroy();
    }
}