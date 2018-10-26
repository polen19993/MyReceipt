package com.polen.receipt.global;

import android.os.Environment;

import com.polen.receipt.api.classes.ReceiptInfo;

import java.io.File;

public class Global {

    public static final String SHARED_PREF_KEY = "com.jeffrey.plate";
    public static final String TAG = "RegisterLocation";
    public static final String PREF_NAME = "CLIENT";
    public final static String CACHE_DIR = ".cache";
    public final static String HOME_DIR = ".CIS";
    public final static String TEMP_DIR = ".temp";
    public final static String CAMERA_TEMP_FILE_NAME = "camera_temp.jpg";
    public final static String CROP_TEMP_FILE_NAME =   "crop_temp.jpg";
    public final static String TEMP_LARGE_IMAGE = "temp_large_image.jpg";


    public static boolean _DEBUG_ = false;

    public static final String TAG_NAME = "Table App";

    public static double nLatitude = Global.DEFAULT_DOUBLE;
    public static double nLongitude = Global.DEFAULT_DOUBLE;
    public static String gAddress;
    public static ReceiptInfo gReceiptInfo;

    //current location
    public static double gCurrentLatitude = Global.DEFAULT_DOUBLE;
    public static double gCurrentLongitude = Global.DEFAULT_DOUBLE;

    public static final String PREF_MY_LATITUDE = "MyLatitude";
    public static final String PREF_MY_LONGITUDE = "MyLongitude";

    public static final String DATE_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String DATE_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    public static final double DEFAULT_DOUBLE = 0;
    public static final long DEFAULT_LONG = 0;

    public static final double	MILE_TO_KM	= 1.60934;
    public static final double	KM_TO_MILE	= 0.621371;


    public static final int AVAILABILITY_CNT = 21;
    public static final String ConnectToInternet = "Please connect to working Internet connection.";

    public static final String USER_PHOTO_DIR = "/upload/user_photos/";

//  public static String SERVER_URL = "http://192.168.0.135/saving-plate";
    public static String SERVER_URL = "http://p-ti.me:8080/webservice";

    public final static int TAKE_GALLERY = 51;
    public final static int TAKE_CAMERA = 52;
    public final static int REQ_CODE_CROP = 40;


    public static String getCacheDirPath(){
        String result = String.format("%s/%s", getHomeDirPath(), CACHE_DIR);
        return result;
    }

    // Directory Paths
    public static String getHomeDirPath(){
        File extStore = Environment.getExternalStorageDirectory();
        return String.format("%s/%s", extStore.getPath(), HOME_DIR);
    }

    public static String getTempDirpath(){
        return String.format("%s/%s", getHomeDirPath(), TEMP_DIR);
    }

    // Temp File Paths.
    public static String getCameraTempFilePath(){
        return String.format("%s/%s", getTempDirpath(), CAMERA_TEMP_FILE_NAME);
    }

    public static String getCropTempFilePath(){
        return String.format("%s/%s", getTempDirpath(), CROP_TEMP_FILE_NAME);
    }

    public static String getLargeImageTempFilePath(){
        return String.format("%s/%s", getTempDirpath(), TEMP_LARGE_IMAGE);
    }
}
