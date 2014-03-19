package com.example.trackme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TackMeActivity extends FragmentActivity {

 	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    
    private static final String TAG = "TrackMe";
    private static LocationManager mLocationManager = null;
    private static boolean mGpsStarted = false;
    private EditText mTextNmea = null;
    private List<GpsSatellite> mSatellites;
    private SatellitesView mSatelliteView = null;
    private SignalsView mSignalsView = null;
    private int mInViewSatNum = 0;
    private int mInUseSatNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tack_me);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(1);
        
        //mSectionsPagerAdapter.instantiateItem(mViewPager, 1);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mSatellites = new ArrayList<GpsSatellite>();
	}
    
    @Override
 	protected void onPause() {
     	Log.i(TAG, "onPause");
 		super.onPause();
 	}

 	@Override
 	protected void onResume() {
 		Log.i(TAG, "onResume");
 		super.onResume();
 	}

 	@Override
 	protected void onStop() {
 		Log.i(TAG, "onStop");
 		super.onStop();
 	}
 	
	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		if (mGpsStarted) {
			mLocationManager.removeGpsStatusListener(mGpsStatusListener);
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager.removeNmeaListener(mGpsNmeaListener);
		}
		mGpsStarted = false;
		super.onDestroy();
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tack_me, menu);
        return true;
    }
    
	private final LocationListener mLocationListener = new LocationListener() {
		public void onProviderDisabled(String provider) {
			Log.d(TAG, "LocationListener onProviderDisabled");
		}

		public void onProviderEnabled(String provider) {
			Log.i(TAG, "LocationListener onProviderEnabled");
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "LocationListener onStatusChanged");
		}

		@Override
		public void onLocationChanged(Location location) {
			Log.i(TAG, "LocationListener onLocationChanged");
			
		}
	};

	private final GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {

		public void onGpsStatusChanged(int event) {
			Log.i(TAG, "onGpsStatusChanged event=" + Integer.toString(event));
			GpsStatus status = mLocationManager.getGpsStatus(null);
			
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				mGpsStarted = true;
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				mGpsStarted = false;
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				int maxSatellites = status.getMaxSatellites();
				int viewSatellites = 0;
				
				Iterable<GpsSatellite> iterable = status.getSatellites();
				Iterator<GpsSatellite> itrator = iterable.iterator();
				mSatellites.clear();
				
				while (itrator.hasNext() && viewSatellites <= maxSatellites) {
					viewSatellites++;
					mSatellites.add(itrator.next());
				}
				
				mSatelliteView = (SatellitesView) DummySectionFragment.stView.findViewById(R.id.satellites_view);
				
				if(mSatelliteView != null){
					mSatelliteView.onUpDate(mSatellites);
				}
				
				mSignalsView = (SignalsView) DummySectionFragment.signalView.findViewById(R.id.signals_view);
				if(mSignalsView != null){
					mSignalsView.onUpDate(mSatellites);
				}

				break;
			}
		}
	};

	private final GpsStatus.NmeaListener mGpsNmeaListener = new GpsStatus.NmeaListener() {
		public void onNmeaReceived(long timestamp, String nmea) {
//			Log.i(TAG, "onNmeaReceived"); 
			String nmeaput;
			if(nmea.indexOf(0) > 0){
				nmeaput = nmea.substring(0,nmea.indexOf(0));
			}else{
				nmeaput = nmea;
			}
			mTextNmea = (EditText) DummySectionFragment.nmeaView.findViewById(R.id.edit_nmea);
			mTextNmea.append(nmeaput);
		}
	};   

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
        	return DummySectionFragment.Instance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_satellites).toUpperCase(l);
                case 1:
                    return getString(R.string.title_signals).toUpperCase(l);
                case 2:
                    return getString(R.string.title_nmea).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        public static View stView;
        public static View signalView;
        public static View nmeaView;
    	private int mNum;
    	
        public DummySectionFragment() {}
        
        static DummySectionFragment Instance(int num){
        	DummySectionFragment f = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, num);
            Log.i(TAG, "getItem, the position is:" + Integer.toString(num)); 
            f.setArguments(args);

        	return f;
        }

        @Override
		public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt(ARG_SECTION_NUMBER) : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	       	
        	switch(mNum){
    			case 0: //satellites sky view
    				stView = inflater.inflate(R.layout.satellites_view, container, false);
    				Log.i(TAG, "onCreateView satellites_view"); 
    				//stView = inflater.inflate(R.layout.satellites_view, null);
    				return stView;
    				
    			case 1: //CN0 block chart  
        			signalView = inflater.inflate(R.layout.signals_view, container, false);
        			Log.i(TAG, "onCreateView signals_view"); 
                    return signalView;

        		case 2: //NMEA data pipe 
        			nmeaView = inflater.inflate(R.layout.nmea_view, container, false);
        			Log.i(TAG, "onCreateView nmea_view"); 
        			return nmeaView;
        			
        		default:
        			return null;
        	}
        }
    }
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(mGpsStarted){
			menu.findItem(R.id.menu_start_stop).setTitle(R.string.menu_stop);
		} else {
			menu.findItem(R.id.menu_start_stop).setTitle(R.string.menu_start);
		}
		return true;
	}    

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		if(!mGpsStarted){
			startGps();
		}else{
			stopGps();
		}
		return true;
    }


	private void stopGps() {
		Log.i(TAG, "stopGps()");
		
		if (mGpsStarted) {
			mLocationManager.removeGpsStatusListener(mGpsStatusListener);
			mLocationManager.removeUpdates(mLocationListener);
			mLocationManager.removeNmeaListener(mGpsNmeaListener);
		}
		mGpsStarted = false;
	}


	private void startGps() {
		Log.i(TAG, "startGps()");
		
		if (!mGpsStarted) {
			Log.i(TAG, "StartGps() normal");
			try {
				mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
				mLocationManager.addGpsStatusListener(mGpsStatusListener);
				mLocationManager.addNmeaListener(mGpsNmeaListener);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "provider is null or doesn't exist");
			} catch (RuntimeException e) {
				Log.e(TAG, "the calling thread has no Looper");
			}
		}
		mGpsStarted = true;
	}
}
