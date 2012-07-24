package com.falacite.swaggerdetector;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import com.falacite.swaggerdetector.DetectorView.DetectorThread;

public class MainActivity extends Activity implements SensorEventListener {

    /**
     * A handle to the thread that's actually running the animation.
     */
    private DetectorThread _detectorThread;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tell system to use the layout defined in our XML file
        setContentView(R.layout.main);

        /*
      A handle to the View in which the game is running.
     */
        DetectorView detectorView = (DetectorView) findViewById(R.id.detectorView);
        _detectorThread = detectorView.getThread();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        _detectorThread.setDetectorPercentage(event.values[1]);
    }
}
