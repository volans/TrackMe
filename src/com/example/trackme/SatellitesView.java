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
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.location.GpsSatellite;

public class SatellitesView extends View {
	private static final String TAG = "SatellitesView";
	
	private static int mScreenWidth = 480; //default value, will be updated automatic align with real screen
	private static int mScreenHeight = 800;
	private static int mCX = mScreenWidth/2; //sky center
	private static int mCY = mScreenWidth/2;
	private static int mR = mScreenWidth/2 - 20;  //sky R
	private static int mSR = mScreenWidth/20;  //satellites R
	
	private List<GpsSatellite> mSatellites;
	
	public SatellitesView(Context context, AttributeSet attrs) {
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
		mCX = mScreenWidth/2;
		mCY = mScreenWidth/2;
		mR = mScreenWidth/2 - 20;
		mSR = mScreenWidth/40;
	}
	
	
	@Override
	public void onDraw(Canvas canvas) {
		drawBackGround(canvas);
		drawSatellite(canvas, mSatellites);
		super.onDraw(canvas);
	}

	public void onUpDate(List<GpsSatellite> satellites) {
		this.mSatellites = satellites;
		invalidate();
	}

	private double degreeToRadian(double degree) {
		return (degree * Math.PI) / 180.0d;
	}
	
	private void drawSatellite(Canvas canvas, List<GpsSatellite> satellites) {
		Log.i(TAG, "drawSatellite()");
		
		double azim;
		float elv;
		float snr;
		int prn;
		Paint paint = new Paint();

		if (satellites != null) {
			for (GpsSatellite satellite : satellites) {

				elv = satellite.getElevation();
				azim = satellite.getAzimuth();
				snr = satellite.getSnr();

				double r2 = mR * ((90.0f - elv) / 90.0f);
				double radian = degreeToRadian(360 - azim + 90);

				double x = mCX + Math.cos(radian) * r2;
				double y = mCY - Math.sin(radian) * r2;

				paint.setStyle(Paint.Style.FILL);
				paint.setColor(getColor((int) snr, satellite.usedInFix()));
				
				prn = satellite.getPrn();
				if (prn < 33 || prn >97) { // Treat GPS, QZSS and SBAS as same system
					canvas.drawCircle((float) x, (float) y, mSR, paint);
				}else if (prn > 64 && prn < 97) { // Glonass
					Path path = new Path();  
			        path.moveTo((float) x, (float) y-mSR);
			        path.lineTo((float) x+(float) (mSR*0.866), (float) y+mSR/2);  
			        path.lineTo((float) x-(float) (mSR*0.866), (float) y+mSR/2);  
			        path.close();
			        canvas.drawPath(path, paint);  
				}
				paint.setColor(Color.WHITE);
				paint.setTextSize(mScreenWidth/16);
				canvas.drawText(""+prn, (float) x+mSR, (float) y+mScreenWidth/40, paint);
			}
		}
	}


	private void drawBackGround(Canvas canvas) {
		float snrx;
		float snry_bottom;
		float snry_top;
		int textsize;
		
		snrx = mScreenWidth/10;
		snry_bottom = mCY+mR+mScreenHeight/10;
		snry_top = mCY+mR+mScreenHeight/12;
		textsize = mScreenWidth/25;
		

		Paint paint = new Paint();
		paint.setColor(0xff181d4b);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(mCX, mCY, mR, paint);
		
		paint.setColor(Color.BLACK);
		paint.setTextSize(textsize);
		canvas.drawText("CN0", snrx, snry_bottom, paint);
		canvas.drawText("00", snrx*2, snry_bottom+textsize, paint);
		canvas.drawText("10", snrx*3, snry_bottom+textsize, paint);
		canvas.drawText("20", snrx*4, snry_bottom+textsize, paint);
		canvas.drawText("30", snrx*5, snry_bottom+textsize, paint);
		canvas.drawText("40", snrx*6, snry_bottom+textsize, paint);
		canvas.drawText("99", snrx*8, snry_bottom+textsize, paint);
		
		canvas.drawText("GPS", snrx*2+mSR, snry_bottom+(float)(textsize*2.3), paint);
		canvas.drawText("Glonass", snrx*5+mSR, snry_bottom+(float)(textsize*2.3), paint);
		
		paint.setColor(getColor(2,true));
		canvas.drawRect(snrx*2, snry_top, snrx*3, snry_bottom, paint);
		paint.setColor(getColor(12,true));
		canvas.drawRect(snrx*3, snry_top, snrx*4, snry_bottom, paint);
		paint.setColor(getColor(22,true));
		canvas.drawRect(snrx*4, snry_top, snrx*5, snry_bottom, paint);
		paint.setColor(getColor(32,true));
		canvas.drawRect(snrx*5, snry_top, snrx*6, snry_bottom, paint);
		paint.setColor(getColor(42,true));
		canvas.drawRect(snrx*6, snry_top, snrx*9, snry_bottom, paint);
		
		canvas.drawCircle(snrx*2, snry_bottom+textsize*2, mSR, paint);
		Path path = new Path();  
        path.moveTo(snrx*5, snry_bottom+textsize*2-mSR);
        path.lineTo(snrx*5+(float) (mSR*0.9), (float) (snry_bottom+textsize*2+mSR/1.4));  
        path.lineTo(snrx*5-(float) (mSR*0.9), (float) (snry_bottom+textsize*2+mSR/1.4));  
        path.close(); 
        canvas.drawPath(path, paint);  

		paint.setColor(Color.GRAY);
		paint.setTextSize(textsize);
		canvas.drawText("N", mCX-textsize/3, mCY-mR+textsize, paint);
		canvas.drawText("90", mCX + mR - textsize, mCY + textsize/3, paint);
		canvas.drawText("180", mCX - textsize, mCY + mR, paint);
		canvas.drawText("270", mCX - mR, mCY + textsize/3, paint);

		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GRAY);
		PathEffect effects = new DashPathEffect(new float[] { 5, 5, 5, 5 }, 1);
		paint.setPathEffect(effects);
		canvas.drawCircle(mCX, mCY, mR, paint);
		canvas.drawCircle(mCX, mCY, mR / 3, paint);
		canvas.drawCircle(mCX, mCY, mR / 3 * 2, paint);
		canvas.drawLine(mCX - mR, mCY, mCX + mR, mCY, paint);
		canvas.drawLine(mCX, mCY - mR, mCX, mCY + mR, paint);
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
}
