package fr.willy.linky;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.content.Context;


public class LineChart{
	
	

	public LineChart(int 							i_ChartWidth, 
			         int 							i_ChartHeight,  
			         int 							i_BgColor, 
			         List<LineChartItem> 			i_LineChartArray, 
			         ArrayList2d<LineChartItem>     i_2dLineChartArray,
			         Context 						i_Context, 
			         ImageView 						i_ImageView,
			         String 						i_title,
			         int                            i_taille_police ) 
	{
	    //------------------------------------------------------------------------------------------
	    // mBackgroundImage  => Image temporaire
	    //                   => On définit la dimension de cette image temporaire
	    //
	    // Bitmap.Config.RGB_565: Each pixel is stored on 2 bytes and only the RGB channels are 
	    // encoded: red   is stored with 5 bits of precision (32 possible values), 
	    //          green is stored with 6 bits of precision (64 possible values)
	    //          blue  is stored with 5 bits of precision. 
	    //------------------------------------------------------------------------------------------
	    Bitmap mBackgroundImage = Bitmap.createBitmap(i_ChartWidth, i_ChartHeight, Bitmap.Config.RGB_565);	

	    
	    //------------------------------------------------------------------
	    // On prépare le graphique
	    //------------------------------------------------------------------
	    
	    View_LineChart LineChartView = new View_LineChart(i_Context);
	    
	    int CHART_LEFT_GAP    = 80; // 40
	    int CHART_RIGHT_GAP   = 20; // 16
	    int CHART_GAP_TOP     = 30;	// 26
	    int CHART_GAP_BOTTOM  = 20;	    
	    boolean mFillGraph    = true;  // fill the graph if true, or draw only a line if false
	    boolean mWhiteGraph   = false; // Set to true => the background is white, else black !!!
	    boolean mAverageLine  = false; // Draw the average line if true => Sum(Y_value)/Y_value_count
	    boolean mZoomChart    = true;   // zoom the graph between the min and max y value, else the graph is draw between 0 -> y max value
	    
	    // colors used if mWhiteGraph == true
	    int mWhiteBgColor     = 0xffffff00;
	    int mWhiteTxtColor    = 0xffffff00;
	    int mWhiteGridColor   = 0xffffff00;
	    int mWhiteLineColor   = 0xffffff00;
	    
	    // colors used if mWhiteGraph == false
	    int mBlackBgColor     = 0xff000000;
	    int mBlackTxtColor    = 0xffffffff;
	    int mBlackGridColor   = 0xff666666;
	    int mBlackLineColor   = 0x9933b5e5;	 //0xaaffbb33;
	    
	    // Initialisation du graphique
	    LineChartView.setGeometry(i_ChartWidth, i_ChartHeight, CHART_LEFT_GAP, CHART_RIGHT_GAP, CHART_GAP_TOP, CHART_GAP_BOTTOM, i_taille_police);
	    
	    if(mWhiteGraph == true) {
	    	LineChartView.setSkinParams(mWhiteBgColor, mWhiteGridColor, mWhiteLineColor, mWhiteTxtColor, mFillGraph, mAverageLine, mZoomChart);
	    } else {
	    	LineChartView.setSkinParams(mBlackBgColor, mBlackGridColor, mBlackLineColor, mBlackTxtColor, mFillGraph, mAverageLine, mZoomChart);
	    }
	    
	    // Initialisation des données
	    LineChartView.setData( i_LineChartArray, 
	    		               i_2dLineChartArray, 
	    		               i_title);
	    
	    LineChartView.invalidate();
	    
	

	    
	    //------------------------------------------------------------------------------------------
	    // Draw on Bitmap canvas
	    //------------------------------------------------------------------------------------------
	    LineChartView.draw( new Canvas(mBackgroundImage) );
	    LineChartView = null;
	    
	    
	    //------------------------------------------------------------------------------------------
	    // Creation d'une nouvelle ImageView to add to main layout
	    //------------------------------------------------------------------------------------------
	    //ImageView mImageView = new ImageView(i_Context);
	    //
	    i_ImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	    //mImageView.setLayoutParams(new LayoutParams(i_ChartWidth, i_ChartHeight));
	    i_ImageView.setBackgroundColor(i_BgColor);
	    i_ImageView.setImageBitmap( mBackgroundImage );
	}
	
}
