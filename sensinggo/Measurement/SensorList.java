package edu.nctu.wirelab.sensinggo.Measurement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import edu.nctu.wirelab.sensinggo.File.FileMaker;
import edu.nctu.wirelab.sensinggo.File.JsonParser;

/**
 * ACCELEROMETER:
 * PROXIMITY
 */
public class SensorList implements SensorEventListener {

    public static float[] gSensorValues = new float[3]; // triaxial acceleration
    public static float[] magneticValues = new float[3];
    public static int lightValue;
    public static String proximityValue;

    private float[] rMatrix = new float[9];    //rotation matrix

    // orientation values, [0]: Azimuth, [1]: Pitch, [2]: Roll
    public static float[] orienValue = new float[3];

    //use for comparison
    private static float[] gSensorValuesTemp = new float[3];
    private static float[] magneticValuesTemp = new float[3];
    private static float[] orienValueTemp = new float[3];

    public SensorList() {}
    //-------------------
    private JsonParser JsonParser = null;

    public void setJsonParser(JsonParser json) {
        JsonParser = json;
    }

    private void initBfRun() {
        gSensorValues[0] = gSensorValues[1] = gSensorValues[2] = 0;
        magneticValues[0] = magneticValues[1] = magneticValues[2] = 0;
        orienValue[0] = orienValue[1] = orienValue[2] = 0;
        lightValue = -1;
        proximityValue = "near";

        gSensorValuesTemp[0] = gSensorValuesTemp[1] = gSensorValuesTemp[2] = 0;
        magneticValuesTemp[0] = magneticValuesTemp[1] = magneticValuesTemp[2] = 0;
        orienValueTemp[0] = orienValueTemp[1] = orienValueTemp[2] = 0;
    }

    /**
     * Register SensorEventListener for following sensors:
     *      Accelerometer, Proximity, Light, Barometer, and Magnetic
     * @param sensorManager access the device's sensors.
     */
    protected void setSensor(SensorManager sensorManager) {
        Sensor mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mAccelerometer == null){
            //No Accelerometer Sensor!
        } else{
            sensorManager.registerListener(this, mAccelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        }

        Sensor mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mProximity == null){
            //No Proximity Sensor!
        } else{
            sensorManager.registerListener(this, mProximity, sensorManager.SENSOR_DELAY_NORMAL);
        }

        // Measures the ambient light level (illumination) in lx.
        Sensor mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLight == null){
            //No mLight Sensor!
        } else{
            sensorManager.registerListener(this, mLight, sensorManager.SENSOR_DELAY_NORMAL);
        }

        // Measures the ambient air pressure in hPa or mbar.
        Sensor mPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (mPressure == null){
            //No mPressure Sensor!
        } else{
            sensorManager.registerListener(this, mPressure, sensorManager.SENSOR_DELAY_NORMAL);
        }

        // Measures the ambient geomagnetic field for all three physical axes (x, y, z) in μT.
        Sensor mMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetic == null){
            //No mMagnetic Sensor!
        } else{
            sensorManager.registerListener(this, mMagnetic, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void startService(SensorManager sensorManager) {
        initBfRun();
        setSensor(sensorManager);
    }

    public void stopService(SensorManager sensorManager) {
        // Accelerometer, Light, Proximity, Barometer, Magnetometer
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        // TODO Auto-generated method stub
        //Writing file in new thread 2018/10/07
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        float[] values = event.values;
                        // Update the value of triaxial acceleration when an axis changed exceed 1
                        if(Math.abs(gSensorValuesTemp[0]-values[0])>=1 || Math.abs(gSensorValuesTemp[1]-values[1])>=1 || Math.abs(gSensorValuesTemp[2]-values[2])>=1){
                            gSensorValuesTemp[0] = values[0];
                            gSensorValuesTemp[1] = values[1];
                            gSensorValuesTemp[2] = values[2];
                            FileMaker.write(JsonParser.sensorInfoToJson("ACCELEROMETER", gSensorValuesTemp[0]+","+gSensorValuesTemp[1]+","+gSensorValuesTemp[2]));
                        }
                        gSensorValues[0] = values[0];
                        gSensorValues[1] = values[1];
                        gSensorValues[2] = values[2];
                        break;

                    case Sensor.TYPE_PROXIMITY:
                        String str;
                        if (event.values[0] == 0) {
                            str = "near";
                        } else {
                            str = "far";
                        }
                        proximityValue = str;
                        Log.d("9487", "TYPE_PROXIMITY: "+event.values[0]);
                        FileMaker.write(JsonParser.sensorInfoToJson("PROXIMITY", str));
                        break;

                    case Sensor.TYPE_LIGHT: //lux
                        if(Math.abs(lightValue-event.values[0])>=10){
                            lightValue = (int)event.values[0];
                            Log.d("9487", "TYPE_LIGHT: "+event.values[0]);
                            FileMaker.write(JsonParser.sensorInfoToJson("LIGHT", ""+event.values[0]));
                        }
                        break;

                    case Sensor.TYPE_PRESSURE: //hPa
                        FileMaker.write(JsonParser.sensorInfoToJson("PRESSURE", ""+event.values[0]));
                        break;

                    case Sensor.TYPE_MAGNETIC_FIELD: // Measures the ambient geomagnetic field for all three physical axes (x, y, z) in μT.
                        float[] mValues = event.values;
                        if(Math.abs(magneticValuesTemp[0]-mValues[0])>=10 || Math.abs(magneticValuesTemp[1]-mValues[1])>=10 || Math.abs(magneticValuesTemp[2]-mValues[2])>=10){
                            magneticValuesTemp[0] = mValues[0];
                            magneticValuesTemp[1] = mValues[1];
                            magneticValuesTemp[2] = mValues[2];
                            FileMaker.write(JsonParser.sensorInfoToJson("MAGNETIC_FIELD", magneticValuesTemp[0]+","+magneticValuesTemp[1]+","+magneticValuesTemp[2]));
                        }
                        magneticValues[0] = mValues[0];
                        magneticValues[1] = mValues[1];
                        magneticValues[2] = mValues[2];
                        break;
                }

                float[] tempValues = new float[3];
                SensorManager.getRotationMatrix(rMatrix, null, gSensorValues, magneticValues);
                SensorManager.getOrientation(rMatrix, tempValues);
                if(Math.abs(orienValueTemp[0]-(float) Math.toDegrees(tempValues[0]))>=15 ||
                        Math.abs(orienValueTemp[1]-(float) Math.toDegrees(tempValues[1]))>=15 ||
                        Math.abs(orienValueTemp[2]-(float) Math.toDegrees(tempValues[2]))>=15
                        ){
                    orienValueTemp[0] = (float) Math.toDegrees(tempValues[0]);
                    orienValueTemp[1] = (float) Math.toDegrees(tempValues[1]);
                    orienValueTemp[2] = (float) Math.toDegrees(tempValues[2]);
                    FileMaker.write(JsonParser.sensorInfoToJson("ROTATION",orienValueTemp[0]+","+orienValueTemp[1]+","+orienValueTemp[2]));

                }
                orienValue[0] = (float) Math.toDegrees(tempValues[0]);
                orienValue[1] = (float) Math.toDegrees(tempValues[1]);
                orienValue[2] = (float) Math.toDegrees(tempValues[2]);
        /*else if(Math.abs(orienValue[1]-(float) Math.toDegrees(tempValues[1]))>=15){
            orienValue[0] = (float) Math.toDegrees(tempValues[0]);
            orienValue[1] = (float) Math.toDegrees(tempValues[1]);
            orienValue[2] = (float) Math.toDegrees(tempValues[2]);
            FileMaker.write(JsonParser.sensorInfoToJson("ROTATION",orienValue[0]+","+orienValue[1]+","+orienValue[2]));
        } else if(Math.abs(orienValue[2]-(float) Math.toDegrees(tempValues[2]))>=15){
            orienValue[0] = (float) Math.toDegrees(tempValues[0]);
            orienValue[1] = (float) Math.toDegrees(tempValues[1]);
            orienValue[2] = (float) Math.toDegrees(tempValues[2]);
            FileMaker.write(JsonParser.sensorInfoToJson("ROTATION",orienValue[0]+","+orienValue[1]+","+orienValue[2]));
        }*/
            }
        }).start();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }
}
