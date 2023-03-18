package com.LSGI541.lab.collection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

public class ConfirmedCasesInTheGreatBayArea extends AppCompatActivity {

    // Declare the widget variables for connecting to the UI
    private Button widget_button_backToMain_confirmedCasesInTheGreatBayArea;
    private MapView widget_mapview_confirmedCasesInTheGreatBayArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_cases_in_the_great_bay_area);

        // ArcGIS Runtime API key
        ArcGISRuntimeEnvironment.setApiKey(getString(R.string.arcgis_allinone_api_key));

        // Set map - all steps
        widget_mapview_confirmedCasesInTheGreatBayArea = findViewById(R.id.mapview_confirmedCasesInTheGreatBayArea);
        ArcGISMap map_confirmedCasesInTheGreatBayArea = new ArcGISMap(BasemapStyle.ARCGIS_LIGHT_GRAY);
        ServiceFeatureTable serviceFeatureTable_confirmedCasesInTheGreatBayArea = new ServiceFeatureTable(getString(R.string.confirmedCasesInTheGreatBayArea_featureLayer_url));
        FeatureLayer featureLayer_confirmedCasesInTheGreatBayArea = new FeatureLayer(serviceFeatureTable_confirmedCasesInTheGreatBayArea);
        map_confirmedCasesInTheGreatBayArea.getOperationalLayers().add(featureLayer_confirmedCasesInTheGreatBayArea);
        widget_mapview_confirmedCasesInTheGreatBayArea.setMap(map_confirmedCasesInTheGreatBayArea);
        widget_mapview_confirmedCasesInTheGreatBayArea.setViewpoint(new Viewpoint(22.305, 114.1795, 50000)); //PolyU coordinate in WGS84, scale 50000

        // Set up the button to go to the MainActivity activity
        widget_button_backToMain_confirmedCasesInTheGreatBayArea = findViewById(R.id.button_backToMain_confirmedCasesInTheGreatBayArea);
        widget_button_backToMain_confirmedCasesInTheGreatBayArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmedCasesInTheGreatBayArea.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    // Pause the MapView when the activity is paused
    @Override
    protected void onPause() {
        widget_mapview_confirmedCasesInTheGreatBayArea.pause();
        super.onPause();
    }

    // Resume the MapView when the activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        widget_mapview_confirmedCasesInTheGreatBayArea.resume();
    }

    // Release the MapView resources when the activity is destroyed
    @Override
    protected void onDestroy() {
        widget_mapview_confirmedCasesInTheGreatBayArea.dispose();
        super.onDestroy();
    }
}