package com.example.jithin.assignment1;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    SQLiteDatabase db;

    EditText patientID;
    EditText Age;
    EditText Name;

    String patientIDText = "";
    String ageText = "";
    String nameText = "";
    //String Sex;

    Button b1, b2;
    int flag = 0;
    LineGraphSeries<DataPoint> series1;
    LineGraphSeries<DataPoint> series2;
    LineGraphSeries<DataPoint> series3;
    int x_cord1=0, x_cord2=0,x_cord3 = 0;
    double x_acc;
    double y_acc;
    double z_acc;
    long time_acc;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

   /* Random r1 = new Random();
    Random r2 = new Random();
    Random r3 = new Random();*/
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        patientID = (EditText) findViewById(R.id.id_text_field);
        Age = (EditText) findViewById(R.id.age_text_field);
        Name = (EditText) findViewById(R.id.name_text_field);
        //Sex = (RadioGroup) findViewById(R.id.radioGroup2);


        //graph View
        final GraphView graph1 = (GraphView) findViewById(R.id.g1); // graph object
        final GraphView graph2 = (GraphView) findViewById(R.id.g2);
        final GraphView graph3 = (GraphView) findViewById(R.id.g3);

        series1 = new LineGraphSeries<DataPoint>();
        series2 = new LineGraphSeries<DataPoint>();
        series3 = new LineGraphSeries<DataPoint>();

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        GridLabelRenderer gridLabel1 = graph1.getGridLabelRenderer();
        GridLabelRenderer gridLabel2 = graph2.getGridLabelRenderer();
        GridLabelRenderer gridLabel3 = graph3.getGridLabelRenderer();
        gridLabel1.setHorizontalAxisTitle("TimeSeries");
        gridLabel1.setVerticalAxisTitle("X");
        gridLabel2.setHorizontalAxisTitle("TimeSeries");
        gridLabel2.setVerticalAxisTitle("Y");
        gridLabel3.setHorizontalAxisTitle("TimeSeries");
        gridLabel3.setVerticalAxisTitle("Z");

        Viewport viewport1 = graph1.getViewport();
        viewport1.setXAxisBoundsManual(true);
        viewport1.setMinX(0);
        viewport1.setMaxX(100);
        viewport1.setYAxisBoundsManual(true);
        viewport1.setMinY(0);
        viewport1.setMaxY(40);

        Viewport viewport2 = graph2.getViewport();
        viewport2.setXAxisBoundsManual(true);
        viewport2.setMinX(0);
        viewport2.setMaxX(100);
        viewport2.setYAxisBoundsManual(true);
        viewport2.setMinY(0);
        viewport2.setMaxY(40);

        Viewport viewport3 = graph3.getViewport();
        viewport3.setXAxisBoundsManual(true);
        viewport3.setMinX(0);
        viewport3.setMaxX(100);
        viewport3.setYAxisBoundsManual(true);
        viewport3.setMinY(0);
        viewport3.setMaxY(40);
        //viewport.setScrollable(true);

        try {
            File dir = new File("/mnt/sdcard/Android/data", "com.example.database");
            try {
                if (!dir.exists()) {
                    dir.mkdir();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            db = SQLiteDatabase.openOrCreateDatabase("/mnt/sdcard/Android/data/com.example.database/Group6.db", null);
        }
        catch (SQLiteException  e){
            //Handle the error
        }

        //STOP button Functionality
        b1 = (Button) findViewById(R.id.button_stop);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                flag = 0;
                graph1.removeAllSeries();// remove 2 more series
                graph2.removeAllSeries();
                graph3.removeAllSeries();
            }
        });
        //RUN button Functionality
        b2 = (Button) findViewById(R.id.button_run);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                patientIDText = patientID.getText().toString();
                ageText = Age.getText().toString();
                nameText = Name.getText().toString();

                // Do something in response to button click
                if(patientIDText.matches("") || ageText.matches("") || nameText.matches("")) {
                    //Insert
                    Toast.makeText(MainActivity.this, "Please enter all Text Fields", Toast.LENGTH_LONG).show();
                }
                else {
                    try{
                        String table_name = nameText+"_"+patientIDText+"_"+ageText;//Sex is missing
                        String query = "CREATE TABLE IF NOT EXISTS " + table_name + "(timestamp TEXT, x TEXT, y TEXT, z TEXT);";
                        db.execSQL(query);
                    }
                    catch (Exception e){
                        //Handle the error
                        Log.e("Error", e.toString());
                    }
                    flag = 1;
                    graph1.addSeries(series1); //2 more graph series
                    graph2.addSeries(series2);
                    graph3.addSeries(series3);
                }
            }
        });
    }

    private void addPlot() {
        // TODO Auto-generated method stub
        series1.appendData(new DataPoint(x_cord1, x_acc), true, 100);
        series2.appendData(new DataPoint(x_cord2, y_acc), true, 100);
        series3.appendData(new DataPoint(x_cord3, z_acc), true, 100);
        x_cord1++;
        x_cord2++;
        x_cord3++;
        try{
            db.beginTransaction();
            String table_name = nameText+"_"+patientIDText+"_"+ageText;//Sex is missing
            String query = "INSERT INTO "+ table_name +" values ('"+x_cord1+"', '"+ x_acc +"', '"+ y_acc +"', '"+ z_acc +"');";
            db.execSQL(query);
        }
        catch (SQLiteException e){
            //Handle the error
            Log.e("Error", e.toString());
        }
        finally {
            db.endTransaction();
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x_acc = event.values[0];
            y_acc = event.values[1];
            z_acc = event.values[2];
            long timestamp = System.currentTimeMillis();
            time_acc = timestamp;
            //System.out.print(x_acc[i]);
            i++;
        }

    }



    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add infinite new entries
                for (; ; ) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (flag == 1) {
                                addPlot();
                            }
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException e) {
                        // manage error ...
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}