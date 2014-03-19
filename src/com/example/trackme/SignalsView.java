package com.example.trackme;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.GpsSatellite;


public class SignalsView extends View {

	private static final String TAG = "SignalView";
	private static int mScreenWidth = 480; //TODO: the screen size should be auto detect
	private static int mScreenHeight = 800;
	
	private static int mHistogramWidth = 400;
	private static int mHistogramHeight = 26;
	private static int mTextSize = 20;
	private static int mHistogramTop = mHistogramHeight *3;
	private static int mHistogramBotom = mHistogramHeight*23;;
	
	
	// The histogram layout of every satellite's signal
	// 10-n0-n1-n2-n3-n4-n5-n6-10
	private static int mSignalRange = (mScreenWidth - 20)/7;
	private static int  mHistogramLeft = mSignalRange +10;
	
	private List<GpsSatellite> mSatellites;
	
	public SignalsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//below code is to align with screen size automatically
		//comment them to get the real layout in satellites_view
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		setDisplayParameters(size.x, size.y);
	}
	
	private void setDisplayParameters(int screenWidth, int screenHeight){
		mScreenWidth = screenWidth;
		mScreenHeight = screenHeight;
		
		mHistogramWidth = screenWidth - 80;
		mTextSize = screenWidth/25;
		mHistogramHeight = mTextSize; 
		
		mHistogramTop = mHistogramHeight *3;
		mHistogramBotom = mHistogramHeight*23;
		mSignalRange = (mScreenWidth - 20)/7;
		mHistogramLeft = mSignalRange +10;
		
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		drawBackGround(canvas);
		drawSatellite(canvas, mSatellites);
		super.onDraw(canvas);
	}

	private void drawSatellite(Canvas canvas, List<GpsSatellite> satellites) {
		Log.i(TAG, "drawSatellite()");
		
		float snr;
		int prn;
		int gpscount = 0;
		int inused = 0;
		Paint paint = new Paint();

		if (satellites != null) {
			for (GpsSatellite satellite : satellites) {

				snr = satellite.getSnr();

				paint.setStyle(Paint.Style.FILL);
				if (satellite.usedInFix()){
					inused +=1;
				}
				paint.setColor(getColor((int) snr, satellite.usedInFix()));

				prn = satellite.getPrn();
				
				gpscount = gpscount + 1;
				float right = (float) mHistogramLeft + (float) ((snr>50?50:snr) * mSignalRange / 10);
				canvas.drawRect(mHistogramLeft, mHistogramTop + (gpscount-1)*mHistogramHeight , right, mHistogramTop + gpscount*mHistogramHeight, paint);
				paint.setColor(Color.BLACK);
				paint.setTextSize(mTextSize);
				
				canvas.drawText(""+prn, 20, mHistogramTop + gpscount*mHistogramHeight, paint);
				canvas.drawText(""+(int)snr, right, mHistogramTop + gpscount*mHistogramHeight, paint);
				
			}
		}
		
	}

	private void drawBackGround(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(mTextSize);
		
		canvas.drawText("0", 10 + mSignalRange, mHistogramHeight, paint);
		canvas.drawText("10", 10 + mSignalRange*2, mHistogramHeight, paint);
		canvas.drawText("20", 10 + mSignalRange*3, mHistogramHeight, paint);
		canvas.drawText("30", 10 + mSignalRange*4, mHistogramHeight, paint);
		canvas.drawText("40", 10 + mSignalRange*5, mHistogramHeight, paint);
		canvas.drawText("CN0", 20, mHistogramHeight*2, paint);
		
		paint.setColor(getColor(2,true));
		canvas.drawRect(10+mSignalRange, mHistogramHeight+6, 10+mSignalRange*6, mHistogramHeight*2 +6, paint);
		paint.setColor(getColor(12,true));
		canvas.drawRect(10+mSignalRange*2, mHistogramHeight+6, 10+mSignalRange*6, mHistogramHeight*2 +6, paint);
		paint.setColor(getColor(22,true));
		canvas.drawRect(10+mSignalRange*3, mHistogramHeight+6, 10+mSignalRange*6, mHistogramHeight*2 +6, paint);
		paint.setColor(getColor(32,true));
		canvas.drawRect(10+mSignalRange*4, mHistogramHeight+6, 10+mSignalRange*6, mHistogramHeight*2 +6, paint);
		paint.setColor(getColor(42,true));
		canvas.drawRect(10+mSignalRange*5, mHistogramHeight+6, 10+mSignalRange*7, mHistogramHeight*2 +6, paint);
		

		
	}
	private int getColor(int snr, Boolean inuse) {
		int color = 0;
		if (inuse) {
			if (snr < 10) {
				color = Color.RED;
			} else if (snr < 20) {
				color = 0xfff47920;
			} else if (snr < 30) {
				color = 0xffffd400;
			} else if (snr < 40) {
				color = 0xffb2d235;
			} else {
				color = 0xff00ff00;
			}
		} else {
			color = Color.GRAY;
		}
		return color;
	}
	
	public void onUpDate(List<GpsSatellite> satellites) {
		this.mSatellites = satellites;
		invalidate();
	}


}
