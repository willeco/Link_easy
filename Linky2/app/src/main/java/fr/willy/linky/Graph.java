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

//Cette classe permet l'affichage des graphiques

public class Graph extends AppCompatActivity {


    protected LineChartView lineChartView;

    Handler handler = new Handler();
    Runnable refresh;

    float dataToShow;
    private List<Float> listData;
    private LineChartData data;
    private String ip_for_sending;
    private String typeOfGraph;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lineChartView = findViewById(R.id.chart);
        setSupportActionBar(toolbar);

        Bundle extras = getIntent().getExtras();
        ip_for_sending = extras.getString("ip_for_sending");
        typeOfGraph = extras.getString("typeOfGraph");
        //A la creation de la classe on fait une demande de tele information.
        HubActivity.ask_tele_info(ip_for_sending,10001);

        TextView uniteeData = findViewById(R.id.textView8);

        if(listData == null)
        {
            listData = new ArrayList<Float>();
        }

        //Le graphique ne s'affiche pas si il n'y a pas au moins 2 valeurs differentes dans la list, on ajoute donc une valeur par defaut.
        if(typeOfGraph.equals("papp"))
        {
            listData.add(151.0f);
            uniteeData.setText("PAPP (V/A)");
        }
        if(typeOfGraph.equals("base"))
        {
            listData.add(Float.parseFloat(HubActivity.base.substring(0,6))-1);
            uniteeData.setText("BASE (kW/h)");

        }

        refresh = new Runnable() {
            public void run() {


                //On parse les valeurs à ajouté en Float
                if(typeOfGraph.equals("papp"))
                {
                    dataToShow = Integer.parseInt(HubActivity.papp);
                }
                if(typeOfGraph.equals("base"))
                {
                    dataToShow = Float.parseFloat(HubActivity.base.substring(0,6));
                }

                //On ajoute la valeur
                listData.add(dataToShow);


                //On gere le defilement du graphique
                if(listData.size() >= 180)
                {
                    Boolean listFullOfSame = true;

                    //On verifie que la liste de points n'est pas constitué entièrement de la même valeur (sans prendre en compte la valeur par défaut) car
                    //Lié au problème précedent, il est toujours nécessaire d'avoir 2 points différents pour afficher le graph.
                    for(int i=1 ; i<listData.size() ; i++)
                    {
                        if(!listData.get(i).equals(listData.get(1)))
                        {
                            listFullOfSame = false;
                        }
                    }

                    //Si la liste possède 2 valeurs différentes on supprime la valeur par défaut
                    if(listFullOfSame == false)
                    {
                        listData.remove(0);
                    }
                    //Sinon on supprime la première valeur après la valeur par défaut
                    else
                    {
                        listData.remove(1);
                    }

                }

                //On affiche le graph
                data = drawInTime(listData);
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


    private LineChartData drawInTime(List listData) {

        String[] axisData = {};


        //These lists will be used to hold the data for Axis and Y-Axis
        List yAxisValues = new ArrayList();
        List axisValues = new ArrayList();

        //Declare and initialize the line which appears inside graph chart, this line will hold the values of Y-Axis
        Line line = new Line(yAxisValues).setColor(Color.parseColor("#148C48"));

        // add Axis and Y-Axis data inside yAxisValues and axisValues lists
        for(int i = 0; i < axisData.length; i++){
            axisValues.add(i, new AxisValue(i).setLabel(axisData[i]));
        }

        for (int i = 0; i < listData.size(); i++){
            float floatData = (float)listData.get(i);
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


        yAxis.setTextColor(Color.parseColor("#CCCCCC"));
        if(typeOfGraph.equals("papp"))
        {
            yAxis.setTextSize(16);
            yAxis.setName("                ");

        }
        if(typeOfGraph.equals("base"))
        {
            yAxis.setTextSize(10);
            yAxis.setName("                ");

        }



        //Au cas ou une data depasse l'axe des Y
        Viewport viewport = new Viewport(lineChartView.getMaximumViewport());
        viewport.top =110;
        lineChartView.setMaximumViewport(viewport);
        lineChartView.setCurrentViewport(viewport);

        return(data);
    }


}
