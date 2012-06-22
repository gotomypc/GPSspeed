package com.biophysics.android.gpsspeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.biophysics.android.gpsspeed.R;

public class mainActivity extends Activity {
    private TextView tvAccuracy;
    private TextView tvAltitude;
    private TextView tvSpeed;
    private TextView tvGpsLat;
    private TextView tvGpsLon;
	private ViewGroup mainLayout;
	private MyLocationListener mLocationListener;
	private LocationManager mLocationManager;
	private int currentSpeed;
	private WakeLock wl;
	private ViewGroup linLayout;
	private MyDrawView myDrawView;
	


    public class MyLocationListener implements android.location.LocationListener {

		public void onLocationChanged(Location location) {
			updateGui(location);
		}
		
		public void onProviderDisabled(String provider) {
//            debug("locationListener onProviderDisabled: " + provider);
		}

		public void onProviderEnabled(String provider) {
//            debug("locationListener onProviderEnabled: " + provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
//            debug("locationListener onStatusChanged: " + provider + "status: " + status);
		}
    	
    }
    
    public class MyDrawView extends View {
    	public MyDrawView(Context context) {
    		super(context);
    	}

    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drive);
        
        
        mainLayout = (ViewGroup)findViewById(R.id.layoutDrive);
        linLayout = (ViewGroup)findViewById(R.id.layoutSign);
        tvAccuracy = (TextView)findViewById(R.id.tvAccuracy);
        tvAltitude = (TextView)findViewById(R.id.tvAltitude);
        tvSpeed = (TextView)findViewById(R.id.tvSpeed);
        tvGpsLat = (TextView)findViewById(R.id.tvGpsLat);
        tvGpsLon = (TextView)findViewById(R.id.tvGpsLon);
        
        myDrawView = new MyDrawView(getBaseContext());
        mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Wake Lock");
        wl.acquire();
        
    }
    
    public void setCurrentSpeedLimit(int newSpeedLimit) {
    	updateGui(null);
    }
    
    public void updateGui(Location location) {

    	if (location != null) {
    		tvAccuracy.setText("Accuracy: " + location.getAccuracy());
			tvAltitude.setText("Altitude: " + location.getAltitude() + " m");
			tvGpsLat.setText(String.format("%3.8f", location.getLatitude()));
			tvGpsLon.setText(String.format("%3.8f", location.getLongitude()));
			tvSpeed.setText(formatSpeed(location.getSpeed()));
			currentSpeed = Math.round(location.getSpeed() * (float)3.6);
			// the above is conversion from meters per sec to km per hour
    	}
    		
			mainLayout.setBackgroundColor(getResources().getColor(R.color.speeding_0));
			tvSpeed.setTextColor(Color.YELLOW);

	}
    

    private String formatSpeed(float speed) {
		return String.format("%3.0f", speed * (float)3.6);
	}

	@Override
    protected void onResume() {
        super.onResume();
        String provider = LocationManager.GPS_PROVIDER;
        long minTime = 0;
        float minDistance = 0;
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, mLocationListener);
        wl.acquire();

    }
    
    @Override
    protected void onStop() {

        super.onStop();

        mLocationManager.removeUpdates(mLocationListener);
        wl.release();
    }
}