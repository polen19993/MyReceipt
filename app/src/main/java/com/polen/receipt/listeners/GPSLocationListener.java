package com.polen.receipt.listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.polen.receipt.global.Global;
import com.polen.receipt.utils.Utils;

public class GPSLocationListener implements LocationListener {

	private Context mContext = null;

	public GPSLocationListener(Context context) {
		this.mContext = context;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {
			Utils.setDoubleSetting(mContext, Global.PREF_MY_LATITUDE, location.getLatitude());
			Utils.setDoubleSetting(mContext, Global.PREF_MY_LONGITUDE, location.getLongitude());
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
