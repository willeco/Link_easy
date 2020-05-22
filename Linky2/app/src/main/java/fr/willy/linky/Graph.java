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


    //Test push master

    protected LineChartView lineChartView;

    Handler handler = new Handler();
    Runnable refresh;

    float papp;
    private List listPapp;
    private LineChartData data;
    private String ip_for_sending;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lineChartView = findViewById(R.id.chart);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        ip_for_sending = extras.getString("ip_for_sending");
        MainActivity.ask_tele_info(ip_for_sending,10001);

        if(listPapp == null)
        {
            listPapp = new ArrayList();
        }

        refresh = new Runnable() {
            public void run() {


                // Do something
                papp = Integer.parseInt(MainActivity.papp);;
                listPapp.add(papp);

                if(listPapp.size() >= 300) //Nombre de points avant défilement
                {
                    listPapp.remove(0);
                }

                data = drawInTime(listPapp);
                lineChartView.setLineChartData(data);

                handler.postDelayed(refresh, 50);
            }
        };
        handler.post(refresh);






        Button button_return_activity = findViewById(R.id.button_return_activity);
        button_return_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private LineChartData drawInTime(List listPapp) {

        String[] axisData = {};


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

        //Enleve les points
        line.setPointColor(5);

        lines.add(line);

        //Add the graph line to the overall data chart
        LineChartData data = new LineChartData();
        data.setLines(lines);


        Axis yAxis = new Axis();
        data.setAxisYLeft(yAxis);


        yAxis.setTextColor(Color.parseColor("#03A9F4"));
        yAxis.setTextSize(16);

        yAxis.setName("PAPP");

        //Au cas ou une data depasse l'axe des Y
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top =110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        return(data);
    }


}
