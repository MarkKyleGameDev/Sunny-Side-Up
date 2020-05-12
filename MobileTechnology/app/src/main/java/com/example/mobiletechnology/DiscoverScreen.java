package com.example.mobiletechnology;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class DiscoverScreen extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private boolean shake = false;
    private View view;
    private long lastUpdateTime;
    private static float SHAKE_THRESHOLD_GRAVITY = 2;

    //TextView placePhone;
   //TextView placePrice;
    //TextView placeName;
    //TextView placeAddress;
    //Button pickPlaceButton;
    private final static int FINE_LOCATION = 100;
    private final static int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_discover_screen);
        requestPermission();

        view = findViewById(R.id.textView_Shake);
        //view.setBackgroundColor(Color.GREEN);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdateTime = System.currentTimeMillis();

        //placePrice = (TextView) findViewById(R.id.placePrice);
        //placePhone= (TextView) findViewById(R.id.placePhone);
       // placeName = (TextView) findViewById(R.id.placeName);
        //placeAddress = (TextView) findViewById(R.id.placeAddress);
        //pickPlaceButton = (Button) findViewById(R.id.pickPlaceButton);
        //pickPlaceButton.setOnClickListener(new View.OnClickListener();
        //Use PlacePicker.IntentBuilder() to construct an Intent//

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(DiscoverScreen.this);
//Create a PLACE_PICKER_REQUEST constant that we’ll use to obtain the selected place//
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected  void onResume(){
        super.onResume();
        //register this class as a listener for the orientation and accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected  void onPause(){
        //unregister listener
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void getAccelerometer(SensorEvent event){
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float gX = x / SensorManager.GRAVITY_EARTH;
        float gY = y / SensorManager.GRAVITY_EARTH;
        float gZ = z / SensorManager.GRAVITY_EARTH;

        //gForce will be close to 1 when there is no movement
        float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        long currentTime = System.currentTimeMillis();
        if(gForce >= SHAKE_THRESHOLD_GRAVITY) //
        {
            if (currentTime - lastUpdateTime < 200) {
                return;
            }
            lastUpdateTime = currentTime;
            Toast.makeText(this, "Device was shaken", Toast.LENGTH_SHORT).show();
            if(shake == !true){
                //view.setBackgroundColor(Color.GREEN);
                startActivity(new Intent(getApplicationContext(), WeatherScreen.class));
            }
            shake = !shake;
        }
    }

    private void requestPermission() {

//Check whether our app has the fine location permission, and request it if necessary//

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
            }
        }
    }

//Handle the result of the permission request//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case FINE_LOCATION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to detect your location!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

/*
//Retrieve the results from the place picker dialog//


    @Override

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

//If the resultCode is OK...//

        if (resultCode == RESULT_OK) {

//...then retrieve the Place object, using PlacePicker.getPlace()//

            Place place = PlacePicker.getPlace(this, data);

//Extract the place’s name and display it in the TextView//
            //placePhone.setText(place.getPhoneNumber());
            placeName.setText(place.getName());

//Extract the place’s address, and display it in the TextView//
            //placePrice.setText(String.valueOf(place.getPriceLevel()));
            placeAddress.setText(place.getAddress());

//If the user exited the dialog without selecting a place...//

        } else if (resultCode == RESULT_CANCELED) {

//...then display the following toast//

            Toast.makeText(getApplicationContext(), "No place selected", Toast.LENGTH_LONG).show();

        }
    }*/
}

