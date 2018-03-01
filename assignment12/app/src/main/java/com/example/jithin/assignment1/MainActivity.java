package com.example.jithin.assignment1;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class MainActivity extends Activity implements SensorEventListener {

    Button b1, b2;
    int flag = 0;
    LineGraphSeries<DataPoint> series1;
    LineGraphSeries<DataPoint> series2;
    LineGraphSeries<DataPoint> series3;
    int x_cord1=0, x_cord2=0,x_cord3 = 0;
    double x_acc; double y_acc; double z_acc; long time_acc[];

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    Random r1 = new Random();
    Random r2 = new Random();
    Random r3 = new Random();
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //x_acc = new double[1000]; y_acc=new double[1000]; z_acc=new double[1000];
        time_acc=new long[1000];
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                // Do something in response to button click

                flag = 1;
                graph1.addSeries(series1); //2 more graph series
                graph2.addSeries(series2);
                graph3.addSeries(series3);


            }
        });

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


    }

    private void addPlot() {
        // TODO Auto-generated method stub
        series1.appendData(new DataPoint(x_cord1, x_acc), true, 100);
        series2.appendData(new DataPoint(x_cord2, y_acc), true, 100);
        series3.appendData(new DataPoint(x_cord3, z_acc), true, 100);
        x_cord1++;
        x_cord2++;
        x_cord3++;

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
            time_acc[i] = timestamp;
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