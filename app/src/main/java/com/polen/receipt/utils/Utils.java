package com.polen.receipt.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.multidex.BuildConfig;
import android.util.Log;
import android.view.Display;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.polen.receipt.R;
import com.polen.receipt.global.Global;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

	public static void printDebug(Context mContext, String msg, Object... args) {
		String strDebug;
		try {
			strDebug = String.format(msg, args);
		} catch (Exception e) {
			strDebug = msg;
		}
		if (Global._DEBUG_ == true) {
			Log.e(Global.TAG_NAME, strDebug);
		}
		if (mContext != null) {
			Toast.makeText(mContext, strDebug, Toast.LENGTH_LONG).show();
		}
	}

	public static boolean isConnectingToInternet(Context mContext) {
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		printDebug(mContext, Global.ConnectToInternet);
		return false;
	}

	public static int getIntSetting(Context mContext, String key) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		return pref.getInt(key, 0);
	}

	public static void setIntSetting(Context mContext, String key, int val) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(key, val);
		editor.commit();
	}

	public static String getStringSetting(Context mContext, String key) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		return pref.getString(key, "");
	}

	public static void setStringSetting(Context mContext, String key, String val) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(key, val);
		editor.commit();
	}

	public static void setDoubleSetting(Context mContext, String key, double val) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putLong(key, Double.doubleToRawLongBits(val));
		editor.commit();
	}

	public static Double getDoubleSetting(Context mContext, String key) {
		SharedPreferences pref = mContext.getSharedPreferences(mContext.getPackageName(), Activity.MODE_PRIVATE);
		double nResult = Double.longBitsToDouble(pref.getLong(key, Global.DEFAULT_LONG));
		return Double.isNaN(nResult) == true ? Global.DEFAULT_DOUBLE : nResult;
	}

	// return ".jpg", ".png"
	public static String getFileExtension(String strFilePath) {
		String strFileExtension = strFilePath.substring(strFilePath.lastIndexOf("."));
		return strFileExtension;
	}

	public static Date parseStringToDate(String strDate) {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat(Global.DATE_DD_MM_YYYY);
		try {
			date = format.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	public static String convertLocalTime(String strOrig, String strFormat) {
		String strResult = "Unknown";
		if (strOrig.length() == 0) {
			return strResult;
		}
		SimpleDateFormat origFormat = new SimpleDateFormat(Global.DATE_YYYY_MM_DD_HH_MM_SS);
		origFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		SimpleDateFormat resultFormat = new SimpleDateFormat(strFormat);
		resultFormat.setTimeZone(TimeZone.getDefault());
		try {
			Date dateOrig = origFormat.parse(strOrig);
			strResult = resultFormat.format(dateOrig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	public static String getUserPhotoUrl(String strPhoto) {
		String strPhotoUrl;
		if (strPhoto.length() == 0) {
			strPhotoUrl = "";
		} else {
			if (strPhoto.startsWith("https://graph.facebook.com") == true) {
				strPhotoUrl = strPhoto;
			} else {
				strPhotoUrl = Global.SERVER_URL + Global.USER_PHOTO_DIR + strPhoto;
			}
		}
		return strPhotoUrl;
	}

	public static int parseIntFromString(String strVal) {
		int nResult = 0;
		try {
			nResult = Integer.parseInt(strVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nResult;
	}

	public static int[] getAvailabilityArray(String strAvailability) {
		int nAvailabilityArray[] = new int[Global.AVAILABILITY_CNT];
		String strArray[] = strAvailability.split(",");
		for (int i=0; i<Global.AVAILABILITY_CNT; i++) {
			nAvailabilityArray[i] = 0;
			if (i < strArray.length) {
				nAvailabilityArray[i] = parseIntFromString(strArray[i]);
			}
		}
		return nAvailabilityArray;
	}

	public static int getMatchedCount(int nAvailabilityArray1[], int nAvailabilityArray2[]) {
		int nMatchedCnt = 0;
		for (int i=0; i<Global.AVAILABILITY_CNT; i++) {
			if (nAvailabilityArray1[i] != 0 && nAvailabilityArray1[i] == nAvailabilityArray2[i]) {
				nMatchedCnt++;
			}
		}
		return nMatchedCnt;
	}


	public static void Log(String tag, String message) {
		Log.i(tag, message);
	}

	public static void Log(String type, String tag, String message) {
		if (BuildConfig.DEBUG) {
			if (type == "i") {
				Log.i(tag, message);
			}
			if (type == "e") {
				Log.e(tag, message);
			}
		}
	}

	@SuppressLint("NewApi")
	public static int getScreenWidth(Activity activity) {
		Point size = new Point();
		Display d = activity.getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			d.getSize(size);
		} else {
			size.x = d.getWidth();
			size.y = d.getHeight();
		}

		return size.x;
	}
	
	public static boolean isConnected(Context context) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager
					.getActiveNetworkInfo();
			return networkInfo != null && networkInfo.isConnected();
		} catch (NullPointerException e) {
			return false;
		}
	}
	
	public static String loadJSONFromAsset(Context ctx, String filename) throws IOException {
    	String json = null;
        try {
            InputStream is = ctx.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
	
	public static void showAlertDialog(Context ctx, String sMsg){
    	AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(sMsg);
		// Set up the buttons
		builder.setPositiveButton(ctx.getResources().getString(R.string.btn_label_ok), null);
		builder.show();
    }
	
	public static void showShortMessage(Context ctx, String sMsg){
    	Toast.makeText(ctx, sMsg, Toast.LENGTH_SHORT).show();
    }
	
	public static void CreateWorkDirectories(String sPath, boolean bDelOldFiles){
    	File file = new File(sPath);
    	File[] children = file.listFiles();
		if (children == null) {
			file.mkdir();
		} else if (bDelOldFiles){
			Calendar today = Calendar.getInstance();		// Get today as a Calendar
	        today.add(Calendar.DATE, -7);  			// Subtract 1 day
	        long _7DaysAgo = today.getTimeInMillis();  
	        
			for (int i=0; i<children.length; i++){
				if (children[i].lastModified() < _7DaysAgo) {
					//Log.e(GlobalConstant.TAG, children[i].getAbsolutePath() + ", " + children[i].getName());
					children[i].delete();
				}
			}
		}
    }
	
	public static String getRealPathFromURI(Activity activity, Uri contentUri) {

        // can post image
        String[] proj={MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
		Cursor cursor = activity.managedQuery( contentUri,
                        proj, // Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); // Order-by clause (ascending by name)
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
	}
	
	public synchronized static int GetExifOrientation(String filepath) 	{
	    int degree = 0;
	    ExifInterface exif = null;
	    
	    try    {
	        exif = new ExifInterface(filepath);
	    } catch (IOException e)  {
	        Log.e("StylePhoto", "cannot read exif");
	        e.printStackTrace();
	    }
	    
	    if (exif != null) {
	        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
	        
	        if (orientation != -1) {
	            // We only recognize a subset of orientation tag values.
	            switch(orientation) {
	                case ExifInterface.ORIENTATION_ROTATE_90:
	                    degree = 90;
	                    break;
	                    
	                case ExifInterface.ORIENTATION_ROTATE_180:
	                    degree = 180;
	                    break;
	                    
	                case ExifInterface.ORIENTATION_ROTATE_270:
	                    degree = 270;
	                    break;
	            }
	        }
	    }
	    
	    return degree;
	}
	
	public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) 	{
	    if ( degrees != 0 && bitmap != null )     {
	        Matrix m = new Matrix();
	        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
	        try {
	            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
	            if (bitmap != b2) {
	            	bitmap.recycle();
	            	bitmap = b2;
	            }
	        } catch (OutOfMemoryError ex) {
	            // We have no memory to rotate. Return the original bitmap.
	        }
	    }
	    
	    return bitmap;
	}
	
	@SuppressWarnings("deprecation")
	public synchronized static Bitmap getSafeDecodeBitmap(String strFilePath, int maxSize) {
		try {
			if (strFilePath == null)
				return null;
			// Max image size
			int IMAGE_MAX_SIZE = maxSize;
			
	    	File file = new File(strFilePath);
	    	if (file.exists() == false) {
	    		//DEBUG.SHOW_ERROR(TAG, "[ImageDownloader] SafeDecodeBitmapFile : File does not exist !!");
	    		return null;
	    	}
	    	
	    	BitmapFactory.Options bfo 	= new BitmapFactory.Options();
	    	bfo.inJustDecodeBounds 		= true;
	    	
			BitmapFactory.decodeFile(strFilePath, bfo);
	        
			if (IMAGE_MAX_SIZE > 0) 
		        if(bfo.outHeight * bfo.outWidth >= IMAGE_MAX_SIZE * IMAGE_MAX_SIZE) {
		        	bfo.inSampleSize = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE
							/ (double) Math.max(bfo.outHeight, bfo.outWidth)) / Math.log(0.5)));
		        }
	        bfo.inJustDecodeBounds = false;
	        bfo.inPurgeable = true;
	        bfo.inDither = true;
	        
	        final Bitmap bitmap = BitmapFactory.decodeFile(strFilePath, bfo);
	    	
	        int degree = GetExifOrientation(strFilePath);
	        
	    	return GetRotatedBitmap(bitmap, degree);
		}
		catch(OutOfMemoryError ex)
		{
			ex.printStackTrace();
			
			return null;
		}
	}
	
	public static Drawable getImageDrawableFromAssetFile(Context context, String assetPath){
		try 
		{
		    InputStream ims = context.getAssets().open(assetPath);
		    Drawable drawable = Drawable.createFromStream(ims, null);
		    return drawable;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static final String getFormattedString(int number){
		return NumberFormat.getNumberInstance(Locale.US).format(number);
	}
//
//public static ResponseData postHttp(Context argContext, String targetURL, String jsonData, boolean secured) throws IOException {
//
//		int cTimeout = 15000;		//connection timeout
//		int sTimeout = 15000;		//so timeout
//
//		HttpClient client =  createHttpClientWithCache(argContext, cTimeout, sTimeout);
//
//		HttpPost request = new HttpPost(targetURL);
//		HttpResponse response = null;
//
//		try {
//			request.setHeader("Content-type","application/json");
//			/*if (secured){
//				String credentials = AppPreference.getUserEmail(argContext) + ":" + AppPreference.getPassword(argContext);
//				String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//				request.addHeader("Authorization", "Basic " + base64EncodedCredentials);
//			}*/
//			request.setEntity(new StringEntity(jsonData, "UTF-8"));
//		} catch (Exception e1) {
//			Log.d("log", "UnsupportedEncodingException");
//			e1.printStackTrace();
//		}
//
//		try {
//			response= client.execute(request);
//		} catch (ClientProtocolException e1) {
//			Log.d("log", "ClientProtocolException");
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			Log.d("log", "Network IOException");
//			throw e1;
//			//e1.printStackTrace();
//		}
//
//		ResponseData responseData = new ResponseData();
//		if(response!=null){
//			try {
//				responseData.setStatusCode(response.getStatusLine().getStatusCode());
//				responseData.setResponse(response.getEntity().getContent());
//			} catch (IllegalStateException e) {
//				Log.d("log", "IllegalStateException");
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("log", "IOException");
//			}
//
//			return responseData;
//		}
//		return responseData;
//	}
//
//	/** Just Getting String Response instead of converting to InputStream **/
//	public static ResponseData getHttp(Context argContext, String targetURL, boolean secured) throws IOException {
//
//		int cTimeout = 60000;		//connection timeout
//		int sTimeout = 60000;		//so timeout
//
//		HttpClient client =  createHttpClientWithCache(argContext, cTimeout, sTimeout);
//
//		HttpGet request = new HttpGet(targetURL);
//		HttpResponse response = null;
//
//		request.addHeader("Connection", "close");
//		/*if (secured){
//			String credentials = AppPreference.getUserEmail(argContext) + ":" + AppPreference.getPassword(argContext);
//			String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//			request.addHeader("Authorization", "Basic " + base64EncodedCredentials);
//		}*/
//
//		try {
//			response= client.execute(request);
//		} catch (ClientProtocolException e1) {
//			Log.d("log", "ClientProtocolException");
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			Log.d("log", "Network IOException");
//			throw e1;
//		}
//
//		ResponseData responseData = new ResponseData();
//		if(response!=null){
//			try {
//				responseData.setStatusCode(response.getStatusLine().getStatusCode());
//				responseData.setResponse(response.getEntity().getContent());
//			} catch (IllegalStateException e) {
//				Log.d("log", "IllegalStateException");
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("log", "IOException");
//			}
//
//			return responseData;
//		}
//		return responseData;
//	}
//
//	public static ResponseData deleteHttp(Context argContext, String targetURL, boolean secured) throws IOException {
//
//		int cTimeout = 15000;		//connection timeout
//		int sTimeout = 15000;		//so timeout
//
//		HttpClient client =  createHttpClientWithCache(argContext, cTimeout, sTimeout);
//
//		HttpDelete request = new HttpDelete(targetURL);
//		HttpResponse response = null;
//
//		request.addHeader("Connection", "close");
//		/*if (secured){
//			String credentials = AppPreference.getUserEmail(argContext) + ":" + AppPreference.getPassword(argContext);
//			String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//			request.addHeader("Authorization", "Basic " + base64EncodedCredentials);
//		}*/
///*		try {
//			request.setHeader("Content-type","application/json");
//		} catch (Exception e1) {
//			Log.d("log","UnsupportedEncodingException");
//			e1.printStackTrace();
//		}*/
//
//		try {
//			response= client.execute(request);
//		} catch (ClientProtocolException e1) {
//			Log.d("log", "ClientProtocolException");
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			Log.d("log", "Network IOException");
//			throw e1;
//		}
//
//		ResponseData responseData = new ResponseData();
//		if(response!=null){
//			try {
//				responseData.setResponse(response.getEntity().getContent());
//				responseData.setStatusCode(response.getStatusLine().getStatusCode());
//			} catch (IllegalStateException e) {
//				Log.d("log", "IllegalStateException");
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("log", "IOException");
//			}
//			//result = convertStreamToString(input, "utf-8");
//			//Log.d("log","result = "+result);
//
//			return responseData;
//		}
//		return responseData;
//	}
//
//	public static ResponseData putHttp(Context argContext, String targetURL, String jsonData, boolean secured) throws IOException {
//
//		int cTimeout = 15000;		//connection timeout
//		int sTimeout = 15000;		//so timeout
//
//		HttpClient client =  createHttpClientWithCache(argContext, cTimeout, sTimeout);
//
//		byte[] postBodyByte; //Data should be changed as byte to send via Entity
//		postBodyByte=jsonData.getBytes();
//
//		HttpPut request = new HttpPut(targetURL);
//		HttpResponse response = null;
//
//		try {
//			HttpEntity httpBody = new ByteArrayEntity(postBodyByte); //Entity which is changed into byte should be changed into HttpEntity
//			request.setHeader("Content-type","application/json"); 	 //This is because we use json format, otherwise you can change as you want
//			/*if (secured){
//				String credentials = AppPreference.getUserEmail(argContext) + ":" + AppPreference.getPassword(argContext);
//				String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//				request.addHeader("Authorization", "Basic " + base64EncodedCredentials);
//			}*/
//
//			request.setEntity(httpBody);
//		} catch (Exception e1) {
//			Log.d("log", "UnsupportedEncodingException");
//			e1.printStackTrace();
//		}
//
//		try {
//			response= client.execute(request);
//		} catch (ClientProtocolException e1) {
//			Log.d("log", "ClientProtocolException");
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			Log.d("log", "Network IOException");
//			throw e1;
//	//		e1.printStackTrace();
//		}
//
//		ResponseData responseData = new ResponseData();
//		if(response!=null){
//			try {
//				responseData.setResponse(response.getEntity().getContent());
//				responseData.setStatusCode(response.getStatusLine().getStatusCode());
//			} catch (IllegalStateException e) {
//				Log.d("log", "IllegalStateException");
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//				Log.d("log", "IOException");
//			}
//			//result = convertStreamToString(input, "utf-8");
//			return responseData;
//		}
//		return responseData;
//	}
//





//	public static synchronized HttpClient createHttpClientWithCache(Context argContext, int cTimeout, int sTimeout) {
//		// Use a session cache for SSL sockets
//		SSLSessionCache sessionCache = argContext == null ? null : new SSLSessionCache(argContext);
//
//		// sets up parameters
//		HttpParams params = new BasicHttpParams();
//		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//		HttpProtocolParams.setContentCharset(params, "utf-8");
//		HttpConnectionParams.setConnectionTimeout(params, cTimeout);
//		HttpConnectionParams.setSoTimeout(params, sTimeout);
//
//		params.setBooleanParameter("http.protocol.expect-continue", false);
//
//		// registers schemes for both http and https
//		SchemeRegistry registry = new SchemeRegistry();
//		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//		final SSLSocketFactory sslSocketFactory = SSLCertificateSocketFactory.getHttpSocketFactory(sTimeout, sessionCache);
//
//		//sslSocketFactory.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//		sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//		registry.register(new Scheme("https", sslSocketFactory, 443));
//
//		//registry.register(new Scheme("https",SSLSocketFactory.getSocketFactory(), 443));
//		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
//		//SingleClientConnManager manager = new SingleClientConnManager(params, registry);
//		return new DefaultHttpClient(manager, params);
//	}
	
	// CONVERT HTTP STREAM TO STRING //
	public static String convertStreamToString(InputStream is, String strCharSet) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,strCharSet));
	        
	        String line = null;
	        
                while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                }
        }
        catch (IOException e) { e.printStackTrace(); }
        finally {
                try { is.close(); }
                catch (IOException e) { e.printStackTrace(); }
        }
        return sb.toString();
	}
	
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
	public static void showKeyboard(Context ctx, boolean bShow, EditText edtBox){
		if (bShow){
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(edtBox, InputMethodManager.SHOW_IMPLICIT);
		}else{
			InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edtBox.getWindowToken(), 0);
		}
	}
	
	public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
 
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
 
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
 
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
 
        // Set the component to be explicit
        explicitIntent.setComponent(component);
 
        return explicitIntent;
    }
	
	/**
	 * returns true if AlwaysFinishActivities option is enabled/checked
	 */
	@SuppressLint("InlinedApi")
	public static boolean isAlwaysFinishActivitiesOptionEnabled(Context appContext) {
	    int alwaysFinishActivitiesInt = 0;
	    if (Build.VERSION.SDK_INT >= 17) {
	        alwaysFinishActivitiesInt = Settings.System.getInt(appContext.getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
	    } else {
	        alwaysFinishActivitiesInt = Settings.System.getInt(appContext.getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
	    }

	    if (alwaysFinishActivitiesInt == 1) {
	        return true;
	    } else {
	        return false;
	    }
	}

	public  static Bitmap decodeFile(String path) {//you can provide file path here
		int orientation;
		try {
			if (path == null) {
				return null;
			}
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 0;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale++;
			}
			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bm = BitmapFactory.decodeFile(path, o2);
			Bitmap bitmap = bm;

			ExifInterface exif = new ExifInterface(path);

			orientation = exif
					.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

			Log.e("ExifInteface .........", "rotation ="+orientation);

//          exif.setAttribute(ExifInterface.ORIENTATION_ROTATE_90, 90);

			Log.e("orientation", "" + orientation);
			Matrix m = new Matrix();

			if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
				m.postRotate(180);
//              m.postScale((float) bm.getWidth(), (float) bm.getHeight());
				// if(m.preRotate(90)){
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				m.postRotate(90);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				m.postRotate(270);
				Log.e("in orientation", "" + orientation);
				bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
						bm.getHeight(), m, true);
				return bitmap;
			}
			return bitmap;
		} catch (Exception e) {
			return null;
		}

	}
}
