package com.polen.receipt.views.imagelist;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;


import com.polen.receipt.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews= Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread

    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService= Executors.newFixedThreadPool(5);
    }
    
    
    
    public void setDefaultId(int resid){
    	stub_id = resid;
    }
    
    int stub_id = 0;;
    public void DisplayImage(String url, ImageView imageView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);

        }else
        {
            queuePhoto(url, imageView, null);
            if (stub_id == 0){
            	imageView.setImageBitmap(null);
            }else{
            	imageView.setImageResource(stub_id);
            }
        }
    }
    public void DisplayImage(String url, ImageView imageView, View tempView)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            if (tempView != null)
            	tempView.setVisibility(View.GONE);
        }else
        {
        	if (tempView != null)
        		tempView.setVisibility(View.VISIBLE);
            queuePhoto(url, imageView, tempView);
            if (stub_id == 0){
            	imageView.setImageBitmap(null);
            }else{
            }
            imageView.setImageResource(stub_id);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView, View tempView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, tempView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            Bitmap bitmap = Utils.getSafeDecodeBitmap(f.getAbsolutePath(), 1000);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public View tempView;
        public PhotoToLoad(String u, ImageView i, View v){
            url=u; 
            imageView=i;
            tempView = v;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null){
                photoToLoad.imageView.setImageBitmap(bitmap);
                if (photoToLoad.tempView != null)
                	photoToLoad.tempView.setVisibility(View.GONE);
            }else{
            	if (stub_id == 0)
            		photoToLoad.imageView.setImageBitmap(null);
                else
                	photoToLoad.imageView.setImageResource(stub_id);
            	if (photoToLoad.tempView != null)
            		photoToLoad.tempView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void clearCache() {
        memoryCache.clear();
//        fileCache.clear();
    }

}
