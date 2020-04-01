package fr.willy.linky;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

import static android.media.CamcorderProfile.get;


public class Graph extends AppCompatActivity {

    protected LineChartView lineChartView;

    Handler handler = new Handler();
    Runnable refresh;
    float papp;
    private List listPapp = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listPapp.add((float)0.0);
        drawInTime(listPapp);

        refresh = new Runnable() {
            public void run() {
                // Do something
                papp = Float.parseFloat(DataHolder.getInstance().getData());
                listPapp.add(papp);

                Toast.makeText(getApplicationContext(), listPapp.toString(), Toast.LENGTH_LONG).show();

                drawInTime(listPapp);

                handler.postDelayed(refresh, 5000);
            }
        };
        handler.post(refresh);



        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lineChartView = findViewById(R.id.chart);

        setSupportActionBar(toolbar);



        Button button_return_activity = findViewById(R.id.button_return_activity);
        button_return_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void drawInTime(List listPapp) {

        String[] axisData = {"Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept",
                "Oct", "Nov", "Dec"};


        //These lists will be used to hold the data for Axis and Y-Axis
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        //Declare and initialize the line which appears inside graph chart, this line will hold the values of Y-Axis
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#9C27B0"));

        // add Axis and Y-Axis data inside yAxisValues and axisValues lists
        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < listPapp.size(); i++){
            float floatData = (float)listPapp.get(i);
            yAxisValues.add(new PointValue(i, floatData));
        }

        //This list will hold the line of the graph chart
        List lines = new ArrayList();
        lines.add(line);

        //Add the graph line to the overall data chart
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //Launch on the app
        lineChartView.setLineChartData(data);

        Axis axis = new Axis();
        axis.setValues(axisValues);
        data.setAxisXBottom(axis);

        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);

        axis.setTextSize(16);
        axis.setTextColor(Color.parseColor("#03A9F4"));

        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);

        yAxis.setName("PAPP");

        //Au cas ou une data depasse l'axe des Y
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top =110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);
    }


}
