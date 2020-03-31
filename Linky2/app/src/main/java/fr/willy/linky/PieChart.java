package fr.willy.linky;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.content.Context;

public class PieChart {

	  /* -------------------------------------------------------------------- */
	  /* CONSTRUCTEUR: PieChart                                               */
	  /* -------------------------------------------------------------------- */
	  public PieChart(	int 							i_ChartWidth, 
		         		int 							i_ChartHeight,  
		         		int 							i_BgColor, 
		         		List<PieDetailsItem> 			PieData,  
		         		Context 						MyContext, 
		         		ImageView 						mImageView, 
		         		int 							i_FontSize ) 
	  {    

	        
	        //------------------------------------------------------------------
	        // Appel constructeur cammbert view ( Cf. View_PieChart.java )
	        //------------------------------------------------------------------
	        View_PieChart PieChartView = new View_PieChart( MyContext );
	        
	        PieChartView.setDefaultParams(i_ChartWidth, i_ChartHeight, i_FontSize);
	        PieChartView.setData(PieData);   // On ajoute les donnees
	        PieChartView.invalidate();

	        //------------------------------------------------------------------------------------------
	        // mBackgroundImage  => Temporary image will be drawn with the content of pie view
	        // Bitmap.Config.RGB_565: Each pixel is stored on 2 bytes and only the RGB channels are 
	        // encoded: red   is stored with 5 bits of precision (32 possible values), 
	        //          green is stored with 6 bits of precision (64 possible values)
	        //          blue  is stored with 5 bits of precision. 
	        //
	        // createBitmap : Returns a mutable bitmap with the specified width and height.
	        //------------------------------------------------------------------------------------------
	        Bitmap mBackgroundImage = Bitmap.createBitmap(i_ChartWidth, i_ChartHeight, Bitmap.Config.RGB_565);	
	        
	        //------------------------------------------------------------------------------------------
	        // Draw Pie  on Bitmap canvas
	        //------------------------------------------------------------------------------------------
	        PieChartView.draw(new Canvas(mBackgroundImage));
	        PieChartView = null;
	        
	        
	        //------------------------------------------------------------------------------------------
	        // Creation d'une nouvelle ImageView to add to main layout
	        //------------------------------------------------------------------------------------------
	        //ImageView mImageView = new ImageView(MyContext);
	        //
	        mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		    //mImageView.setLayoutParams(new LayoutParams(Size, Size));
		    mImageView.setBackgroundColor(i_BgColor);
		    mImageView.setImageBitmap( mBackgroundImage );
		    
	  }
	  

	  
}
