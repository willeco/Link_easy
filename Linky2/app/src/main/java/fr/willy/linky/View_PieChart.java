package fr.willy.linky;

import java.text.DecimalFormat;
import java.util.List;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.BitmapFactory.Options;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class View_PieChart extends View  {

	// Toutes les variables sont definies prealablement afin d'accelerer le graphisme
	private static final int   		WAIT             = 0;
	private static final int   		IS_READY_TO_DRAW = 1;
	private static final int   		IS_DRAW          = 2;
	private static final float 		START_INC        = 0;
	
	private Paint 					mBgPaints     = new Paint();	// Objet servant a definir la couleur de fond d'une portion de camembert
	private Paint 					mLinePaints   = new Paint();	// Objet servant a definir la couleur du trait d'une portion de camembert
	private Paint 					mTextPaints   = new Paint();    // Objet servant a definir la legende
	private Paint 					mLegendPaints = new Paint();	// Objet servant a definir la legende
	//private int   mOverlayId;
	
	private int   					mWidth;
	private int   					mHeight;
	private int   					mGapLeft;
	private int   					mGapRight;
	private int   					mGapTop;
	private int   					mGapBottom;
	private int                     mFontSize;
	
	private int   					mBgColor;
	private int   					mLineColor;
	
	
	private int   					mState = WAIT;					// Etat de l'automate
	private float 					mStart;
	private float 					mSweep;
	private int   					mMaxConnection;
	private float 					mPercent;
	
	// Liste des donnees a afficher
	private List<PieDetailsItem> 	mDataArray;
	
	// Legend
	private int 					mHrectLegend;
	private int 					mWrectLegend;
	private int 					mBottomGapLegend;
	private int 					y1;
	private int 					y2;
	private int 					x1;
	private int 					x2;
	private DecimalFormat    		m_df1;
	private DecimalFormat    		m_df2;
	
	//--------------------------------------------------------------------------------------
	// Constructeur n°1
	//--------------------------------------------------------------------------------------	
	public View_PieChart (Context context)
	{
		super(context);		// Appelle le constructeur de la classe mere (View)
		mLineColor 		= 0xff000000;
		mHrectLegend    = 10;
		mWrectLegend    = 20;
		m_df1        	= new DecimalFormat("0"); 
		m_df2        	= new DecimalFormat("0.0"); 
	}
	
	//--------------------------------------------------------------------------------------
	// Constructeur n°2
	//--------------------------------------------------------------------------------------	
	public View_PieChart(Context context, AttributeSet attrs) 
	{
        super(context, attrs);	
        mLineColor      = 0xff000000;
        mHrectLegend    = 10;
        mWrectLegend    = 20;
        m_df1        	= new DecimalFormat("0");
        m_df2        	= new DecimalFormat("0.0"); 
	}
	
    //--------------------------------------------------------------------------------------
    @Override 
    protected void onDraw(Canvas canvas) 
    {
    	// Called when the view should render its content.
    	super.onDraw(canvas);
    	
    	//----------------------------------------------------------------------------------
    	if (mState != IS_READY_TO_DRAW) return;
    	canvas.drawColor(mBgColor);
    	
    	//----------------------------------------------------------------------------------
    	// Le camembert sera affiche avec un certain niveau de transparence
    	mBgPaints.setAntiAlias(true);
    	mBgPaints.setStyle(Paint.Style.FILL);
    	mBgPaints.setColor(0x88FF0000); 
    	mBgPaints.setStrokeWidth(0.5f);
    	
    	//----------------------------------------------------------------------------------
    	mLinePaints.setAntiAlias(true);
    	mLinePaints.setStyle(Paint.Style.STROKE);
    	mLinePaints.setColor(mLineColor);
    	mLinePaints.setStrokeWidth(1.5f);

    	mLegendPaints.setAntiAlias(true);
    	mLegendPaints.setStyle(Paint.Style.STROKE);
    	mLegendPaints.setColor(mLineColor);
    	mLegendPaints.setStrokeWidth(0.8f);
    	
    	//------------------------------------------------------
    	mTextPaints.setStyle(Paint.Style.FILL);
    	mTextPaints.setColor(mLineColor);
    	mTextPaints.setTextSize((float)mFontSize);

		//----------------------------------------------------------------------------------
		// Calcul taille en nombre de pixels occupee par la police de caracteres
		//----------------------------------------------------------------------------------
    	Rect 			TextRect = new Rect();
		String someText 			= "1";
		mTextPaints.getTextBounds(someText, 0, someText.length(), TextRect);		
		int m_hauteur_lettre 		    = TextRect.bottom - TextRect.top;
		int m_largeur_lettre 		    = TextRect.right  - TextRect.left;
		mHrectLegend = m_hauteur_lettre;
		mWrectLegend = 3 * m_largeur_lettre;
		
    	//------------------------------------------------------
    	// Create a new rectangle with the specified coordinates.
    	// to draw the pie chart.
    	//------------------------------------------------------
    	//RectF mOvals = new RectF( mGapLeft, mGapTop, mWidth - mGapRight, mHeight - mGapBottom);
    	int taille_pie = (int) Math.ceil( mHeight/1.5 );   	
    	RectF mOvals   = new RectF( mGapLeft, mGapTop, taille_pie , taille_pie);
    	//------------------------------------------------------
    	mStart = START_INC;
    	PieDetailsItem Item;
    	
    	// Calculate grand total count
    	mMaxConnection = 0;
    	for (int i = 0; i < mDataArray.size(); i++) 
    	{
    		mMaxConnection += (float)mDataArray.get(i).Count;
        }   	
    	
    	// Prepare legend
    	// mBottomGapLegend   = ( mHeight - 2*mDataArray.size()*mHrectLegend ) / 2;
    	//y1             = mBottomGapLegend;
    	//x1             = (int)(mHeight*1.1);
    	y1               = taille_pie + (int)(1.5*m_hauteur_lettre);
    	x1               = (int)(3* m_largeur_lettre);    	
    	x2               = x1 + mWrectLegend;
    	
    	// Elaborate PieChart
    	for (int i = 0; i < mDataArray.size(); i++) 
    	{
    		Item     = (PieDetailsItem) mDataArray.get(i);
    		mBgPaints.setColor(Item.Color);
    		mPercent =  (float) 100 * ( (float)Item.Count / (float)mMaxConnection );
    		mSweep   = (float) 3.60 * mPercent;
    		
    		// Oval   : The bounds of oval used to define the shape and size of the arc
    		// mStart : Starting angle (in degrees) where the arc begins
    		// mSweep : Sweep angle (in degrees) measured clockwise
    		// If true, include the center of the oval in the arc, and close it if it is being stroked. This will draw a wedge
    		// 
    		canvas.drawArc(mOvals, mStart, mSweep, true, mBgPaints);
    		canvas.drawArc(mOvals, mStart, mSweep, true, mLinePaints);
    		mStart += mSweep;
    		
    		
    		// Legend float (left, float top, float right, float bottom)
    		y2 = y1+mHrectLegend;
    		canvas.drawRect(x1, y1, x2, y2, mBgPaints    ); // Fond
    		canvas.drawRect(x1, y1, x2, y2, mLegendPaints); // Trait autour de la légende
    		
    		// texte associe a la legende
    		canvas.drawText(Item.Label + "  (" + m_df1.format(Item.Count) 
    				                   + " = "  + m_df2.format(mPercent) 
    				                   + "%)", (float)(x2+m_largeur_lettre), y2, mTextPaints);

    		y1 += (1.5*m_hauteur_lettre);
        }

    	
    	//------------------------------------------------------
    	//if (false)
    	//{
    	//Options options = new BitmapFactory.Options();
        //options.inScaled = false;
    	//Bitmap OverlayBitmap = BitmapFactory.decodeResource(getResources(), mOverlayId, options);
    	//canvas.drawBitmap(OverlayBitmap, 0.0f, 0.0f, null);
    	//}
    	
    	//------------------------------------------------------
    	mState = IS_DRAW;
    }
    //--------------------------------------------------------------------------------------
    public void setDefaultParams(int i_Width, int i_Height, int i_FontSize) 
    {
    	setLayoutParams(new LayoutParams(i_Width, i_Height));
    	mWidth     = i_Width;
   	 	mHeight    = i_Height;
   	 	mGapLeft   = 10;
   	 	mGapRight  = 10;
   	 	mGapTop    = 10;
   	 	mGapBottom = 10;  	
    	setGeometry(mWidth, mHeight, mGapLeft, mGapRight, mGapTop, mGapBottom);
   	 	mBgColor   = 0xff000000;
   	    mLineColor = 0xffffffff;
   	    mFontSize  = i_FontSize;
    }
    
    //--------------------------------------------------------------------------------------
    // (Private) Permet de definir les dimensions
    //--------------------------------------------------------------------------------------
    private void setGeometry(int width, int height, int GapLeft, int GapRight, int GapTop, int GapBottom) {
    	mWidth     = width;
   	 	mHeight    = height;
   	 	mGapLeft   = GapLeft;
   	 	mGapRight  = GapRight;
   	 	mGapTop    = GapTop;
   	 	mGapBottom = GapBottom;
   	 	//mOverlayId = OverlayId;
    }
    
    //--------------------------------------------------------------------------------------
    // Permet de definir la couleur de fond
    //--------------------------------------------------------------------------------------
    public void setBgColor(int bgColor) {
   	 	mBgColor   = bgColor;
    }
    
    //--------------------------------------------------------------------------------------
    // Permet de definir la couleur des traits
    //--------------------------------------------------------------------------------------
    public void setLineColor(int LineColor) {
   	 	mLineColor   = LineColor;
    }
    
    //--------------------------------------------------------------------------------------
    // Methode permettant de fournir les donnees a afficher
    //--------------------------------------------------------------------------------------   
    public void setData(List<PieDetailsItem> data) 
    {
    	mDataArray     = data;
    	mState         = IS_READY_TO_DRAW; // Automate pret a afficher
    }
    
    //--------------------------------------------------------------------------------------
    // Methode permettant de definir l'etat de l'automate
    //--------------------------------------------------------------------------------------
    public void setState(int State) {
    	mState = State;
    }
    
    //--------------------------------------------------------------------------------------
    // Permet de recuperer la couleur d'une tranche en fonction de l'index
    //--------------------------------------------------------------------------------------
    public int getColorValue( int Index ) 
    {
   	 	if (mDataArray == null) return 0;
   	 	
   	 	if (Index < 0) {
   	 		return ( (PieDetailsItem)mDataArray.get(0)                   ).Color;
   	 	} else if (Index >= mDataArray.size()) {
   	 		return ( (PieDetailsItem)mDataArray.get(mDataArray.size()-1) ).Color;
   	 	} else {
   	 		return ( (PieDetailsItem)mDataArray.get(Index)               ).Color;
   	 	}
    }
    
}
