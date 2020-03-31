package fr.willy.linky;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fr.willy.linky.LineChartItem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class View_LineChart extends View {
	

	public static int       	MAX_CAR_LABELS     = 10;
	public static int       	MAX_CAR_FORM       = 13;
	private static double   	EPSILON_GRAD       = 2.0e-6;
	
	public enum TypeFormat   { entier, reel, exposant; }	
	
	private Canvas 				mCanvas          = null;
	
	// Pinceaux
	// -----------------------------------------------------------------------	
	private Paint  				mBGPaint         = new Paint();
	private Paint  				mLinePaint       = new Paint();
	private Paint  				mGridPaintFull   = new Paint();
	private Paint  				mGridPaintDot    = new Paint();
	private Paint  				mTitlePaint      = new Paint();
	private Paint  				mAveragePaint    = new Paint();
	private Paint  				mAverageTxtPaint = new Paint();

	// Automate
	// -----------------------------------------------------------------------
	private boolean  			isReadyToDraw = false;
	
	// Options
	// -----------------------------------------------------------------------
	private boolean  			mFillGraph;
	private boolean  			mAverageLine;
	private boolean  			mZoomChart;
	
	// Taille graphique
	// -----------------------------------------------------------------------
	private int      			m_width_pixels;
	private int      			m_height_pixels;	
	
	private int      			m_gap_left_pixels;
	private int      			m_gap_right_pixels;
	private int      			m_gap_top_pixels;
	private int      			m_gap_bottom_pixels;
	
	private int                 m_FontSize;
	
	private float 				m_hauteur_lettre;
	private float 				m_largeur_lettre;
	private float 				m_max_largeur_text_axe_y;
	
	
	// Indices couleurs
	// -----------------------------------------------------------------------	
	private int      			mBgColor;
	private int      			mGridColor;
	private int      			mLineColor;
	private int      			mTextColor;
	
	private String   			mChartTitle;

	// Axes
	// -----------------------------------------------------------------------	
	private InfosAxis 			xAxis = new InfosAxis();
	private InfosAxis 			yAxis = new InfosAxis();
	
	// Donnees a afficher
	// -----------------------------------------------------------------------	
	List<LineChartItem>        	mLineChartArray   = new ArrayList<LineChartItem>();
	ArrayList2d<LineChartItem> 	m2dLine           = new ArrayList2d<LineChartItem>();
	
	//------------------------------------------------------------------------
	// Regroupe les informations associ�es a un axe
	//------------------------------------------------------------------------	
	private class InfosAxis { 
		  
		  public double          m_min;						// Min
		  public double          m_max; 					// Max
		  public double          m_round_min;				// Min approch�
		  public double          m_round_max;				// Max approch�
		  public double          m_LargDiv;					// Grande divisions
		  
		  public int             m_nb_div;					// Nombre de divisions
		  public int             m_nb_subdiv;            	// Nombre de sous-divisions
		  public int             m_exposant;             	// Exposant
		  
		  // --------------------------------
		  public float           m_scale;					// Conversion Valeurs vers Pixels
		  public float           m_drawOffset;				// Offset pour conversion vers Pixels
		  
		  // --------------------------------
	      public TypeFormat      m_type_format;     		// Type de format                
		  public int             m_ndec;            		// Nombre de decimales           
		  public  int            m_occupation;      		// Occupation totale (not used)            
		  String                 m_format;
		  String                 m_df_format;				// Format etiquette
		  public InfosAxis() {
			  
		  }
	  
	  }
	  
	
	//--------------------------------------------------------------------------------------
	public View_LineChart (Context context){
		super(context);
	}
	
	//--------------------------------------------------------------------------------------
	public View_LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
	}
	
	//--------------------------------------------------------------------------------------
	@Override 
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if (isReadyToDraw == false) return;
		mCanvas = canvas;
		
		//--------------------------------------------------------------------------
		mBGPaint.setStyle(Paint.Style.FILL);
		mBGPaint.setColor(mBgColor);
		mCanvas.drawPaint(mBGPaint);
		
		//--------------------------------------------------------------------------
		mLinePaint.setColor(mLineColor);
		if (mFillGraph == true){
			mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
		} else {
			mLinePaint.setStyle(Paint.Style.FILL);
		}
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStrokeWidth(1.5f);


		//--------------------------------------------------------------------------
		// Definition du type de Trait a utiliser pour tracer la grille
		//--------------------------------------------------------------------------
		mGridPaintDot.setColor(mGridColor);
		mGridPaintDot.setAntiAlias(false);
		mGridPaintDot.setStyle(Paint.Style.STROKE);
		mGridPaintDot.setStrokeWidth(0.5f);
		mGridPaintDot.setPathEffect( new DashPathEffect(new float[] {5,5 }, 1) );
		
		//--------------------------------------------------------------------------
		// Definition du type de trait a utiliser pour tracer les axes
		//--------------------------------------------------------------------------
		mGridPaintFull.setColor(mTextColor);
		mGridPaintFull.setAntiAlias(false);
		mGridPaintFull.setStyle(Paint.Style.STROKE);
		mGridPaintFull.setStrokeWidth(1.0f);
		
		//--------------------------------------------------------------------------
		// Definition de la police utilise pour Titre du graphique
		//--------------------------------------------------------------------------
		//  Recommended dimension type for text is "sp" for scaled-pixels (example: 15sp).
		mTitlePaint.setStyle(Paint.Style.FILL);
		mTitlePaint.setTextAlign(Paint.Align.LEFT);
		mTitlePaint.setColor(mTextColor);
		mTitlePaint.setTextSize( m_FontSize );
		
		//--------------------------------------------------------------------------
		Rect 			TextRect = new Rect();
		
		//--------------------------------------------------------------------------
		LineChartItem 	ChartItem;
		float 			NewX;
		float 			NewY;
		float 			graphAverage = 0;
		
		//--------------------------------------------------------------------------
		// Recuperation des valeurs MIN & MAX en x et en y
		//--------------------------------------------------------------------------
		double   		maxY;
		double   		minY;
		double   		maxX;
		double  		minX;
		
		maxY = minY = m2dLine.get(0, 0).Y;
		maxX = minX = m2dLine.get(0, 0).X;
		for (int j = 0; j< m2dLine.getNumRows(); j++)
		{
			for (int i = 0; i<m2dLine.getNumCols(j); i++)
			{
				ChartItem = m2dLine.get(j, i);
				if (maxY < ChartItem.Y) maxY = ChartItem.Y;
				if (maxX < ChartItem.X) maxX = ChartItem.X;
				if (minY > ChartItem.Y) minY = ChartItem.Y;
				if (minX > ChartItem.X) minX = ChartItem.X;			
				graphAverage += ChartItem.Y;
			}
		}
		
		
		
		//----------------------------------------------------------------------------------
		// Options
		//----------------------------------------------------------------------------------			
		if (mZoomChart == false) {minY = 0;}
			
		graphAverage = graphAverage / (float)m2dLine.getNumCols(0);
		
		//----------------------------------------------------------------------------------
		// SET Axis Divisions
		//----------------------------------------------------------------------------------		
		yAxis.m_min = minY;
		yAxis.m_max = maxY;
		calculer_divisions(yAxis);	// Calcul nombre de divisions et sous divisions
		rechercher_format(yAxis);
		
		xAxis.m_min = minX;
		xAxis.m_max = maxX;
		calculer_divisions(xAxis); // Calcul nombre de divisions et sous divisions
		rechercher_format(xAxis);
		
		//----------------------------------------------------------------------------------
		// Calcul taille en nombre de pixels occupee par la police de caracteres
		//----------------------------------------------------------------------------------
		String someText 			= "1";
		mTitlePaint.getTextBounds(someText, 0, someText.length(), TextRect);		
		m_hauteur_lettre 		    = TextRect.bottom - TextRect.top;
		m_largeur_lettre 		    = TextRect.right  - TextRect.left;
		
		DecimalFormat 	df 			= new DecimalFormat(yAxis.m_df_format);
		someText 			        = df.format(yAxis.m_round_max);
		mTitlePaint.getTextBounds(someText, 0, someText.length(), TextRect);
		m_max_largeur_text_axe_y    = TextRect.right - TextRect.left;
		
		//----------------------------------------------------------------------------------
		// Redefinition des Gaps en fonction de la taille occup�e par la police de caracteres
		//----------------------------------------------------------------------------------
		m_gap_bottom_pixels 		= (int) Math.ceil(2.5 * m_hauteur_lettre);
		m_gap_top_pixels    		= m_gap_bottom_pixels;
		m_gap_left_pixels   		=  (int) Math.ceil(1.2 * m_max_largeur_text_axe_y);
		m_gap_right_pixels          = m_gap_left_pixels;
		//----------------------------------------------------------------------------------
		// SET Coordinate transformations
		//----------------------------------------------------------------------------------	
		yAxis.m_scale      = -( m_height_pixels - m_gap_top_pixels  - m_gap_bottom_pixels )  / (float) ( yAxis.m_round_max - yAxis.m_round_min);
		xAxis.m_scale      = +( m_width_pixels  - m_gap_left_pixels - m_gap_right_pixels  )  / (float) ( xAxis.m_round_max - xAxis.m_round_min);
		xAxis.m_drawOffset = (float) m_gap_left_pixels;
		yAxis.m_drawOffset = (float) (m_height_pixels - m_gap_bottom_pixels);
		
		//----------------------------------------------------------------------------------
		// Trace les axes X et Y
		//----------------------------------------------------------------------------------
		draw_Yaxis( yAxis, mCanvas); 
        draw_Xaxis( xAxis, mCanvas);
		
		//----------------------------------------------------------------------------------
        // Affichage des courbes
		//----------------------------------------------------------------------------------
		for (int j = 0; j< m2dLine.getNumRows(); j++)
		{
			if (j > 0) mLinePaint.setColor(0x88ffbb33); //0xff99cc00
			
			
			if (mFillGraph == false)
			{
			
				for (int k = 1; k<m2dLine.getNumCols(j); k++)
				{
					mCanvas.drawLine(convert(xAxis, m2dLine.get(j,k-1).X ), 
							         convert(yAxis, m2dLine.get(j,k-1).Y ), 
							         convert(xAxis, m2dLine.get(j,k  ).X ), 
							         convert(yAxis, m2dLine.get(j,k  ).Y ), mLinePaint);
				}
				
			} else {
			
				Path mPath = new Path();
				mPath.rewind();
				
				for (int k = 0; k<m2dLine.getNumCols(j); k++)
				{	
					NewX      = convert(xAxis, m2dLine.get(j,k  ).X );
					NewY      = (j==0) ? convert(yAxis, m2dLine.get(j,k  ).Y ) : convert(yAxis, m2dLine.get(j-1,k  ).Y + m2dLine.get(j,k  ).Y ) ;	
					if (k==0){
						mPath.moveTo(NewX, NewY);	
					} else {
						mPath.lineTo(convert(xAxis, m2dLine.get(j,k  ).X ), NewY);
					}
				}
				for (int k = m2dLine.getNumCols(j)-1; k>=0; k--)
				{	
					NewX      = convert(xAxis, m2dLine.get(j,k  ).X );
					NewY      = (j==0) ? yAxis.m_drawOffset : convert(yAxis, m2dLine.get(j-1,k  ).Y ) ;												
					mPath.lineTo(NewX, NewY);	
				}
					
				mPath.close();
				mCanvas.drawPath(mPath, mLinePaint);
				mCanvas.drawPath(mPath, mGridPaintFull);
			}
		}

		//-----------------------------------------------------------------------------------
		// Average Line
		//-----------------------------------------------------------------------------------
		if (mAverageLine == true)
		{
			mAveragePaint.setColor(			0xffff0000			);
			mAveragePaint.setStyle(			Paint.Style.FILL	);
			mAveragePaint.setAntiAlias(		true				);
			mAveragePaint.setStrokeWidth(	1.5f				);
			mAveragePaint.setPathEffect( 	new DashPathEffect(new float[] {5, 5}, 1) ); 
			
			//----------------------------------------------------------------------
			mAverageTxtPaint.setStyle(    	Paint.Style.FILL	);
			mAverageTxtPaint.setTextAlign(	Paint.Align.LEFT	);
			mAverageTxtPaint.setColor(    	0xffff0000			);
			mAverageTxtPaint.setTextSize(	10					);
			
			//----------------------------------------------------------------------
			DecimalFormat FloatFormatter 	= new DecimalFormat("0.##");
			String StrAverage 				= "Mean:" + FloatFormatter.format (graphAverage);
			mAverageTxtPaint.getTextBounds(StrAverage, 0, StrAverage.length(), TextRect);
			
			NewX = (float)( m_width_pixels - m_gap_right_pixels - TextRect.right) - 3.0f;
			NewY = (float)( m_gap_top_pixels - TextRect.top) + 3.0f;
			mCanvas.drawText(StrAverage, NewX, NewY, mAverageTxtPaint);
			
			NewY = convert(yAxis,graphAverage );
			
			mCanvas.drawLine(m_gap_left_pixels, NewY, m_width_pixels - m_gap_right_pixels, NewY, mAveragePaint);
		}
		
		//--------------------------------------------------------------------------
		// Affichage du titre
		//--------------------------------------------------------------------------
		// public void getTextBounds (String text, int start, int end, Rect bounds)
		// start : Index of the first char in the string to measure
		// end   : 1 past the last char in the string measure
		// On recupere les dimensions du titre au sein de la variable TextRect
		// The rectangle is represented by the coordinates of 
		// its 4 edges (left, top, right bottom)
		// 
		//--------------------------------------------------------------------------
		String Title = mChartTitle;
		mTitlePaint.getTextBounds(Title, 0, Title.length(), TextRect);
		NewX = (m_width_pixels - TextRect.right)/2;
		NewY = TextRect.bottom - TextRect.top;
		
		// drawText(String text, float x, float y, Paint paint)
		mCanvas.drawText(Title, NewX, NewY , mTitlePaint);
		

	}
	
	//------------------------------------------------------------------------------
	// Convertit une coordonn�e de la courbe (x ou y) en coordonn�e �cran
	//------------------------------------------------------------------------------
	private float convert( InfosAxis axis, double In) 
	{
		return(axis.m_drawOffset + ( axis.m_scale * (float) ( In -  axis.m_round_min) ));
	}
	
	//--------------------------------------------------------------------------------------
	// Definit le cadre dans lequel sera trac� la courbe
	//--------------------------------------------------------------------------------------
	public void setGeometry(int width, int height, int GapLeft, int GapRight, int GapTop, int GapBottom, int FontSize) 
	{
		m_width_pixels     = width;
		m_height_pixels    = height;
		m_gap_left_pixels   = GapLeft;
		m_gap_right_pixels  = GapRight;
		m_gap_top_pixels    = GapTop;
		m_gap_bottom_pixels = GapBottom;
		m_FontSize          = FontSize;
	}
	
	//--------------------------------------------------------------------------------------
	public void setSkinParams(int bgColor, int gridColor, int lineColor, int textColor, boolean FillGraph, boolean AverageLine, boolean ZoomChart) 
	{
		mBgColor     = bgColor;
		mGridColor   = gridColor;
		mLineColor   = lineColor;
		mTextColor   = textColor;
		mFillGraph   = FillGraph;
		mAverageLine = AverageLine;
		mZoomChart   = ZoomChart;
	}
	
	
	//--------------------------------------------------------------------------------------
	// Methode pour initialiser les donnees
	// -------------------------------------------------------------------------------------
	public void setData( List<LineChartItem>          i_ChartArray, 
			             ArrayList2d<LineChartItem>   i_2dLineChartArray, 
			             String                       i_titre) 
	{
		mLineChartArray    = i_ChartArray;
		m2dLine  		   = i_2dLineChartArray;
		mChartTitle        = i_titre;
		isReadyToDraw      = true;
	}
	
	//--------------------------------------------------------------------------------------
	// Trace l'axe des X
	//--------------------------------------------------------------------------------------
	private void draw_Xaxis(  InfosAxis axis , Canvas mCanvas) 
	{
		float 			step      = (float) axis.m_LargDiv;		

		// Definition des coordonnees d'affichage des labels
		float 			X1;
		float 			Y1   		= m_gap_top_pixels;
		float 			Y2   		= (float) ( m_height_pixels - m_gap_bottom_pixels) ;
		float 			Ylabel 		= (float) ((m_height_pixels - m_gap_bottom_pixels) + m_hauteur_lettre*1.5); 
		float 			value;
		DecimalFormat 	df 			= new DecimalFormat(axis.m_df_format);
		
		//Rect            TextRect 	= new Rect();
		String 			StrLablel;
		
		mTitlePaint.setTextAlign(Paint.Align.CENTER);
		
		// Boucle sur toutes les divisions
		for (int i = 0; i <= axis.m_nb_div; i++)
		{
			// Calcul du Label
			value     = (float) axis.m_round_min + i * step;	
			X1        = convert(axis, value);
			
			// Affichage de la grille ou de l'axe
			if (i == 0) 
			{
				mCanvas.drawLine(X1, Y1, X1, Y2, mGridPaintFull);
			} else {  
				mCanvas.drawLine(X1, Y1, X1, Y2, mGridPaintDot);
			}
			
			// Formattage de l'etiquette
			
			//String StrLablel = String.format(axis.m_format, value ); 
			//StrLablel = "" + value; 
			
			StrLablel = df.format(value);
			//mTitlePaint.getTextBounds(StrLablel, 0, StrLablel.length(), TextRect);

			mCanvas.drawText(StrLablel, X1, Ylabel, mTitlePaint);
		}
		mTitlePaint.setTextAlign(Paint.Align.LEFT);
	}
	
	//-----------------------------------------------------------------------------------
	// Trace l'axe des Y
	//-----------------------------------------------------------------------------------
	private void draw_Yaxis(  InfosAxis axis , Canvas mCanvas) 
	{
		double 			step  = axis.m_LargDiv;
		float  			X1, Y1, X2;
		float  			value;
		String 			StrLablel;
		//Rect            TextRect 	= new Rect();
		DecimalFormat 	df 			= new DecimalFormat(axis.m_df_format);
		X1   						= (float) (m_gap_left_pixels - 2);
		X2   						= (float) (m_width_pixels   - m_gap_right_pixels);
		mTitlePaint.setTextAlign(Paint.Align.RIGHT);
		for (int i = 0; i <= axis.m_nb_div; i++) 
		{
			// Calcul du Label
			value     = (float)axis.m_round_min + (float)(i * step);
			Y1        = convert(axis, value);
			
			if (i == 0) 
			{
				mCanvas.drawLine(X1, Y1, X2, Y1, mGridPaintFull);
			} else { 
				mCanvas.drawLine(X1, Y1, X2, Y1, mGridPaintDot);
			}
			
			// Affichage du label
			//StrLablel = "" + value;
			StrLablel = df.format(value);
			//mTitlePaint.getTextBounds(StrLablel, 0, StrLablel.length(), TextRect);
			mCanvas.drawText(StrLablel, m_gap_left_pixels - m_largeur_lettre, (Y1 ), mTitlePaint);
		}
		mTitlePaint.setTextAlign(Paint.Align.LEFT);
	}	
	
	//--------------------------------------------------------------------------------------
	// V�rfie si deux flotants sont proches l'un de l'autre
	//--------------------------------------------------------------------------------------
	private boolean egal( double A, double B) 
	{
		if  ( Math.abs(A-B) < EPSILON_GRAD )
		{
			return true;
		} else {
			return false;
		}
	}
	
	
	
	//--------------------------------------------------------------------------------------
	// Function: calculer_divisions
	//
	//			 Initialise la structure axis contenant les informations suivantes
	// 				- m_round_min  
    //				- m_round_max 
    //				- m_exposant 
    //				- m_LargDiv 
    //				- m_nb_div
    //				- m_nb_sousdiv
	//
	// En Java, La r�gle est la suivante : 
    //		- ton param�tre est un objet, alors il est pass� par r�f�rence 
	//	    - ton param�tre est un type primitif, alors il est pass� par valeur (par copie) 
	//--------------------------------------------------------------------------------------	
	private boolean  calculer_divisions(InfosAxis axis)
	{
	int   NW = 3;                    /* Nb colonnes de tab_divisions      */
	int   NL = 3;                    /* Nb lignes de tab_divisions        */
	int   IND_R = 0;                 /* Indice du tableau tab_divisions   */
	int   IND_H = 1;                 /* Indice du tableau tab_divisions   */
	int   IND_S = 2;                 /* Indice du tableau tab_divisions   */
	double    a, b;                  /* Bornes                            */
	double    al, bl;                /* Intervalle reduit                 */
	double    larg_div;              /* largeur d'une division            */
	double    amax;                  /* Maximum des bornes                */
	double    tolerance;             /* Erreur maximale toleree           */
	double    range;                 /* Longueur de l'intervalle          */
	double    range_min, range_max;  /* Intervalles min et max            */
	double    scale;                 /* Facteur d'echelle                 */
	int       ie;                    /* Exposant                          */
	int       i;                     /* Indice de boucle                  */
	boolean      retour;             /* Code retour                       */
	boolean      premiere_passe;     /* Indicateur de sortie              */

	/* Table pour le calcul du nombre de divisions */
	int       tab_divisions[][] = {
	          { 10, 20, 50 },        /* Largeur de l'intervalle           */
	          {  1,  2,  5 },        /* Largeur de la division            */
	          { 10,  2,  5 }         /* Nombre de sous-divisions          */
	          };

	  if (axis.m_min > axis.m_max || egal(axis.m_min, axis.m_max) ) {
	    retour = false;
	  } else {

	    retour         = true;
	    a              = axis.m_min;
	    b              = axis.m_max;
	    amax           = axis.m_max;
	    premiere_passe = true;

	    /*
	     * Calcul erreur maximale tolerance, egale a 0.0002 % de la plus
	     * grande des bornes car EPSILON_GRAD = 2.e-6
	     * -------------------------------------------------------------------
	     */
	    tolerance = Math.max(Math.abs(a), Math.abs(b))* EPSILON_GRAD;

	    /*
	     * Reduction de l'intervalle de facon a permettre des erreurs d'arrondis
	     * Reduction de 0.0004 % car EPSILON_GRAD = 2.e-6
	     * ---------------------------------------------------------------------
	     */
	    al = axis.m_min + tolerance;
	    bl = axis.m_max - tolerance;

	    /* Calcul de la longueur de l'intervalle en coordonnees utilisateur */
	    range = bl - al;

	    if ( range < 0.0 ) {
	      /*
	       * Intervalle negatif ou trop petit : Definition impossible
	       * Inferieur a 0.0004 % de la plus grande des bornes en valeur absolue
	       */
	      return false;
	    }

	    // Initialisation exposant et facteur d'echelle
	    ie    = 0;
	    scale = 1.0;

	    /*
	     * A l'origine, range_min = 5 et range_max = 50
	     * on cherche un intervalle voisin de 10, 20 ou 50
	     * -----------------------------------------------
	     */
	    range_max = (double) tab_divisions[IND_R][NW-1];
	    range_min = range_max / 10.0;

	    /*
	     * Calcul de range pris entre range_min et range_max tel
	     * que range = range (d'origine) / scale
	     * Calcul du facteur d'echelle : scale = 10^ie et de l'exposant : ie
	     * -----------------------------------------------------------------
	     */
	     while (true)
	     {
		    while (range <  range_min) { range *= 10.0; scale /= 10.0; ie--; }
		    while (range >= range_max) { range /= 10.0; scale *= 10.0; ie++; }
	
		    // Recherche dans le tableau tab_divisions l'intervalle qui contient range
		    i = 0;
		    if (false)
		    {
		    	while ( (range <= (double) tab_divisions[IND_R][i]) && (i < (NW-1)) ) {i++;}
		    } else {
			    for (i=0; i<NW; i++) {
			      if ( range <= (double ) tab_divisions[IND_R][i]) break;
			    }
			    if (i==NW) i -= 1;
		    }
		    
		    // Calcul de la largeur d'une division
		    larg_div = (double) tab_divisions[IND_H][i] * scale ;
	
		    /*
		     * Calcul des nouvelles bornes en coordonnees utilisateurs
		     * -------------------------------------------------------
		     * borne inferieure multiple de H et proche de AL par defaut
		     * borne superieure multiple de H et proche de BL par exces
		     */
	
		    a = Math.floor((double) al / larg_div) * larg_div;
		    b = Math.floor((double) bl / larg_div) * larg_div;
		    if (a > al) a -= larg_div;
		    if (b < bl) b += larg_div;
	
		    // On verifie si les operations realisees n'ont pas reduit l'intervalle de trop
		    a = Math.min(a, (double) axis.m_min);
		    b = Math.max(b, (double) axis.m_max);
	
		    // Ne pas executer au second passage
		    if (premiere_passe) {
	
		      premiere_passe = false;
	
		      // Calcul de l'erreur maximale toleree 
		      amax       = Math.max (Math.abs(a), Math.abs(b));
		      tolerance  = amax * EPSILON_GRAD;
	
		      // Reduction de l'intervalle 
		      al = a + tolerance;
		      bl = b - tolerance;
	
		      // Calcul de la longueur de l'intervalle
		      range = (bl - al) / scale ;
	
		      // si largeur modifiee, on effectue un second passage, mais avec a et b 
		      if (range > (double) tab_divisions[IND_R][i])
		      {
		    	  continue;
		      } else {
		    	  break;
		      }
	
		    } else {
		    	break; // On quitte la boucle au plus tard apr�s deux passes
		    }
	    } // Fin de la boucle while sans fin
	     
	    // Calcul du nombre de divisions et de sous-divisions
	    axis.m_nb_div    = (int) ((b - a + .5 * larg_div) / larg_div) ;
	    axis.m_nb_subdiv = tab_divisions[IND_S][i];

	    /*
	     * Calcul du facteur d'echelle de la borne maximale
	     * ------------------------------------------------
	     * L'exposant ie est celui de la valeur maximale
	     * amax = range.10^ie avec 1<= range< 10
	     */
	    range      = (amax + 0.5 * larg_div) / scale;
	    range_min  = 1.0;
	    range_max  = 10.0;

	    while (range <  range_min) { range *= 10.0; scale /= 10.0; ie--; }
	    while (range >= range_max) { range /= 10.0; scale *= 10.0; ie++; }

	    axis.m_round_min        = a;
	    axis.m_round_max        = b;
	    axis.m_exposant  = ie;
	    axis.m_LargDiv  = larg_div;

	  }
	  return retour;
	}

	//--------------------------------------------------------------------------------------
	//
	// NOM      :  rechercher_format
	//
	// ROLE     :  Cette fonction rechercher le format adapte a la representation
	//             des graduations sur un axe. Le format entier est utilise si
	//             possible, puis le format flottant est essaye et finalement le
	//             format E
	//--------------------------------------------------------------------------------------
	void rechercher_format(InfosAxis axis)
	{
	int             SEUIL =4;       /* Seuil pour selection format         */
	int             ms_digit;       /* Pos. digit le plus significatif     */
	int             ls_digit;       /* Pos. digit le moins significatif    */
	int             ms,ns;          /*                                     */
	int             occupation;     /* Occupation des digits               */
	int             ndec;           /* Nombre de decimales                 */
	double          tolerance;      /* Tolerance                           */
	double          heps;           /* Erreur sur la largeur de division   */
	double          reps;           /* Erreur relative sur (B - A)         */
	int             ir;             /* Digit non representatif             */

	
	  // Position du digit le plus significatif (positive ou negative)
	  ms_digit = axis.m_exposant;
	  ms       = Math.max(ms_digit, 0);

	  //Calcul position digit le moins significatif a partir de l'erreur relative de (B - A).
	  tolerance = Math.max (Math.abs(axis.m_round_min), Math.abs(axis.m_round_max)) * EPSILON_GRAD;
	  reps      = 2 * tolerance / (axis.m_round_max - axis.m_round_min);

	  // Erreur faite sur la largeur de division (regle de 3)
	  heps      = axis.m_LargDiv * reps;

	  // Position du dernier digit significatif 
	  ls_digit  = (int) Math.floor((Math.log10 (2.0 * heps)) - (double) ms_digit) + ms_digit;

	  /*
	   * Digit non representatif ramene au format entier
	   * ----------------------------------------------
	   *  (Lard_div + heps) devient Larg_div car sinon le format entier
	   * n'est jamais propose.
	   */
	  ir       = (int) (0.5 + axis.m_LargDiv * Math.pow((double)10 ,(double)(-ls_digit)));

	  // Recherche du plus petit digit significatif different de zero
	  while ((ir % 10) == 0) {
	    ir       /= 10;
	    ls_digit +=1;
	    if (ls_digit >= ms_digit) break;
	  }

	  // Nombre de digits significatifs
	  ns = ms_digit + 1 - ls_digit;

	  // Calcul l'occupation, la place des decimales, et le format a utiliser 
	  ndec = Math.max(-ls_digit, 0);

	  /* Calcul du nombre de digits (comprend les zeros) pour le format I ou F
	   *  = Exposant + point + nombre de decimales
	   */
	  occupation = ms + 1 + ndec;

	  /*
	   * Recherche du format le plus adapte (Entier, reel ou exposant)
	   * -------------------------------------------------------------
	   * Si les formats I et F sont selectionnes, on teste le format E
	   * pour savoir si il peut occuper moins de place, car on sait qu'il
	   * occupe ns + SEUIL
	   */

	  if ((ndec == 0) &&
	       ((occupation + 1) <= MAX_CAR_LABELS) &&
	       ((ns + SEUIL) >= (occupation + 1))) {

	    // Format entier (occupation du signe en plus)
		axis.m_type_format   = TypeFormat.entier;
	    occupation      += 1;
	    axis.m_format    =  "%d";
	    axis.m_df_format =  "0";
	    
	  } else {

	    if (((occupation + 2) <= MAX_CAR_LABELS) &&
	        ((ns + SEUIL    ) >= (occupation + 2)  )    ) 
	    {
	      // Format flottant (occupation du signe et du point en plus)
	      axis.m_type_format   = TypeFormat.reel;
	      occupation          += 2;
	      axis.m_format       =  "%."+ ndec + "f"; 	// Use String.format
	      StringBuffer       stbug = new StringBuffer("0.");
	      for (int i=0; i<ndec; i++) { stbug.append("0"); }
	      //axis.m_df_format    =  "0.0";						// A revoir
	      axis.m_df_format    =  stbug.toString();				// A revoir
	    } else {
	      /*
	       * Format exposant (occupation du signe, du point, du facteur 10,
	       * du signe et 2 digits en plus)
	       * --------------------------------------------------------------
	       */
	      axis.m_type_format  = TypeFormat.exposant;
	      ndec                = Math.min(MAX_CAR_LABELS - SEUIL, ns);
	      occupation          = ndec + SEUIL;
	      axis.m_format       = "%." + (ndec - 1) + "e";
	      
	      StringBuffer       stbug = new StringBuffer("0.");
	      for (int i=0; i<ndec; i++) { stbug.append("0"); }
	      stbug.append("e0");
	      
	      //axis.m_df_format    =  "0.0";				// A revoir
	      axis.m_df_format    =  stbug.toString();				// A revoir
	      axis.m_df_format    =  "0.0";
	    }

	  }
	  axis.m_occupation = occupation;
	  axis.m_ndec       = ndec;
	  
	  return ;
	}	
}
