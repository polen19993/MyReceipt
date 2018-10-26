package com.polen.receipt.global;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GlobalFunc {
    public static String ROOT_WORKING_FOLDER = Environment.getExternalStorageDirectory() + "/receipt";

    public static String getTempFolderPath() {

        File root_folder = new File(ROOT_WORKING_FOLDER);
        if(!root_folder.exists()){
            root_folder.mkdirs();
        }
        String strPath = ROOT_WORKING_FOLDER + "/temp";
        new File(strPath + "/").mkdirs();
        return ROOT_WORKING_FOLDER + "/temp";
    }

    static int __tmp_no = 0;
    public static int getTempFileNo() {
        return __tmp_no++;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
//        }
        return null;
    }


    public static Bitmap getBitmap(String filepath, Context context, int size){

        try{
            Uri uri = Uri.parse("file://"+filepath);
            InputStream in = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();
            int scale = 1;
            while (o.outWidth * o.outHeight * (1.0D / Math.pow(scale, 2.0D)) > size){
                scale++;
            }

            Bitmap b;
            in = context.getContentResolver().openInputStream(uri);
            if (scale <= 1){
                b = BitmapFactory.decodeStream(in);
            }else{
                o = new BitmapFactory.Options();
                o.inJustDecodeBounds = false;
                o.inSampleSize = scale - 1;
                b = BitmapFactory.decodeStream(in, null, o);
                double height = b.getHeight();
                double width = b.getWidth();
                double y = Math.sqrt((double)size / (width / height));
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int)(y / height * width), (int)y, true);
                if (!b.sameAs(scaledBitmap)){
//  	          		b.recycle();
                }
                b = scaledBitmap;

                System.gc();
            }
            in.close();

            ExifInterface exif;
            int orientation = 0;
            try {
                exif = new ExifInterface(filepath);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
                b = rotate(b, 90);
            }else if (orientation == ExifInterface.ORIENTATION_ROTATE_180){
                b = rotate(b, 180);
            }else if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
                b = rotate(b, 270);
            }
            return b;

        }catch (IOException e){
            return null;
        }

    }

    private static Bitmap rotate(Bitmap image, int i){
        if(i == 0 || image == null){
            return image;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(i, image.getWidth(), image.getHeight());
        Bitmap bitmap2;
        try{
            int j = image.getWidth();
            int k = image.getHeight();
            bitmap2 = Bitmap.createBitmap(image, 0, 0, j, k, matrix, true);
        }catch(OutOfMemoryError e){
            e.printStackTrace();
            return image;
        }
        if(image == bitmap2){
            return bitmap2;
        }
        image.recycle();
        image = bitmap2;
        return image;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String filepath){
        try {
            File file = new File(filepath);
            if( file.exists() )
            {
                file.delete();
                file = new File(filepath);
            }
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();
        }catch(Exception e){

        }
    }

    public static void copyFile(String inputPath, String outputFolder, String outputFileName) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File (outputFolder);
            if (!dir.exists())
            {
                dir.mkdirs();
            }



            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputFolder + "/" + outputFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

        }  catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    public static String getFileFolder(String file_name)
    {
        String strPath = getUserFolder() + "/" + file_name;
        new File(strPath + "/").mkdirs();
        return strPath;
    }

    public static String getUserFolder()
    {
//        String strPath = ROOT_WORKING_FOLDER + "/" + SharedPreferenceHelper.getSharedPreferenceHelper().getLoginInfo().username;
        String strPath = ROOT_WORKING_FOLDER + "/" + "image";
        new File(strPath + "/").mkdirs();
        return strPath;
    }

    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(latitude1);
        locationA.setLongitude(longitude1);
        Location locationB = new Location("B");
        locationB.setLatitude(latitude2);
        locationB.setLongitude(longitude2);
        distance = locationA.distanceTo(locationB);
        return distance;
    }
}
