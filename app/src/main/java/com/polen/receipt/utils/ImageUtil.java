package com.polen.receipt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.polen.receipt.ui.CircleBitmapDisplayer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;


import android.graphics.Bitmap.Config;

import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.polen.receipt.MainApplication;
import com.polen.receipt.R;

/**
 * Created by kong on 7/1/14.
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";


    public static void displayImage(ImageView view, String path, ImageLoadingListener listener) {
        if (path.startsWith("http") == false && path.startsWith("drawable") == false) {
            path = "file://" + path;
        }
        ImageLoader loader = ImageLoader.getInstance();
        try {
            loader.displayImage(path, view, DEFAULT_DISPLAY_IMAGE_OPTIONS, listener);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            loader.clearMemoryCache();
        }
    }

    public static void displayRoundImage(ImageView view, String path, ImageLoadingListener listener) {
        if (path.startsWith("http") == false && path.startsWith("drawable") == false) {
            path = "file://" + path;
        }
        ImageLoader loader = ImageLoader.getInstance();
        try {
            loader.displayImage(path, view, ROUND_DISPLAY_IMAGE_OPTIONS, listener);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            loader.clearMemoryCache();
        }
    }

    public static void loadImage(String path, ImageLoadingListener listener) {
        ImageLoader loader = ImageLoader.getInstance();
        try {
            loader.loadImage(path, DEFAULT_DISPLAY_IMAGE_OPTIONS, listener);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    private static final DisplayImageOptions.Builder DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .displayer(new FadeInBitmapDisplayer(300, true, false, false))
            .showImageForEmptyUri(R.drawable.app_icon)
            .showImageOnLoading(R.drawable.app_icon)
            .showImageOnFail(R.drawable.app_icon).cacheOnDisk(true)
            .cacheInMemory(true).bitmapConfig(Config.ARGB_8888);

    private static final DisplayImageOptions.Builder ROUND_DISPLAY_IMAGE_OPTIONS_BUIDLER = new DisplayImageOptions.Builder()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .displayer(new FadeInBitmapDisplayer(300, true, false, false))
            .showImageForEmptyUri(R.drawable.app_icon)
            .showImageOnLoading(R.drawable.app_icon)
            .showImageOnFail(R.drawable.app_icon).cacheOnDisk(true)
            .cacheInMemory(true).bitmapConfig(Config.ARGB_8888);

    private static final DisplayImageOptions DEFAULT_DISPLAY_IMAGE_OPTIONS = DEFAULT_DISPLAY_IMAGE_OPTIONS_BUIDLER.build();
    private static final DisplayImageOptions ROUND_DISPLAY_IMAGE_OPTIONS = ROUND_DISPLAY_IMAGE_OPTIONS_BUIDLER.displayer(new RoundedBitmapDisplayer(1000)).build();

    /**
     * Converts a bitmap to grayscale
     *
     * @param bmpOriginal
     * @return
     */
    public static Bitmap toGrayScale(Bitmap bmpOriginal) {
        if (bmpOriginal == null) {
            return null;
        }
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        // RGB_565 uses half the amount of pixels than ARGB_8888. Since this will be used for a notification,
        // and is just gray we'll use RGB_565 to save on some memory
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    /**
     * Loads a gif into an image view. The gif must have been saved to the disk cache before calling this method or it will fail
     *
     * @param imageView   The ImageView where the gif will be displayed
     * @param url         The url of the image. This is the key for the cached image
     * @param imageLoader The Imageloader where we will retrieve the image from
     * @return if successful
     */
    public static boolean loadAndDisplayGif(@Nullable ImageView imageView, @NonNull String url, @NonNull ImageLoader imageLoader) {
        File file = DiskCacheUtils.findInCache(url, imageLoader.getDiskCache());
        return loadAndDisplayGif(imageView, file);
    }

    /**
     * Loads a gif into an image view. The gif must have been saved to the disk cache before calling this method or it will fail
     *
     * @param imageView The ImageView where the gif will be displayed
     * @param file      File of the gif
     * @return
     */
    public static boolean loadAndDisplayGif(@Nullable ImageView imageView, @Nullable File file) {
        if (imageView == null) return false;

        if (FileUtil.isFileValid(file)) {
            try {
                imageView.setImageDrawable(new GifDrawable(file));
                return true;
            } catch (IOException e) {
                LogUtil.e(TAG, "Unable to play gif", e);
            }
        } else {
            LogUtil.w(TAG, "Gif file is invalid");
        }

        return false;
    }

    /**
     * Initializes the ImageLoader
     *
     * @param context App context
     */
    public static void initImageLoader(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        File cacheDir = StorageUtils.getCacheDirectory(context);
        int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());
        // System.out.println("dsa-->"+MAXMEMONRY+"   "+(MAXMEMONRY/5));//.memoryCache(new
        // LruMemoryCache(50 * 1024 * 1024))

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheExtraOptions(480, 800).defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(MAXMEMONRY / 5))
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .imageDecoder(new BaseImageDecoder(false)) // default
                .defaultDisplayImageOptions(getDefaultDisplayOptions().build())
                .build();

        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().destroy();
        }

        ImageLoader.getInstance().init(config);
    }

    /**
     * Returns the display options for the image loader when loading for the gallery.
     * Fades in the images when loaded from the network. Also uses Bitmap.Config.RGB_565 for less memory usage
     *
     * @return
     */
    public static DisplayImageOptions.Builder getDisplayOptionsForGallery() {
        return getDefaultDisplayOptions()
                .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY);
    }

    /**
     * Returns the display options for viewing an image in the view activity
     * Uses a placeholder whiling loading images
     *
     * @return
     */
    public static DisplayImageOptions.Builder getDisplayOptionsForView() {
        return getDefaultDisplayOptions()
                .showImageOnLoading(new ColorDrawable(Color.TRANSPARENT));
    }

    public static DisplayImageOptions.Builder getDisplayOptionsForComments() {
        return getDefaultDisplayOptions()
                .displayer(new CircleBitmapDisplayer(MainApplication.getInstance().getResources()));
    }

    public static DisplayImageOptions.Builder getDisplayOptionsForFullscreen() {
        return getDefaultDisplayOptions()
                .cacheInMemory(false);
    }

    public static DisplayImageOptions.Builder getDisplayOptionsForPhotoPicker() {
        return getDisplayOptionsForGallery()
                .cacheOnDisk(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED);
    }

    /**
     * Returns the default display options for the image loader
     * Resets view before loading, caches in memory and on disk.
     *
     * @return
     */
    public static DisplayImageOptions.Builder getDefaultDisplayOptions() {
        return new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .cacheOnDisk(true);
    }

    /**
     * Returns the images rotation from it's EXIF data
     *
     * @param file Image file
     * @return EXIF rotation, Undefined if no orientation was obtained
     */
    public static int getImageRotation(File file) {
        if (!FileUtil.isFileValid(file)) {
            return ExifInterface.ORIENTATION_UNDEFINED;
        }

        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } catch (Exception e) {
            LogUtil.e(TAG, "Unable to get EXIF data from file", e);
        }

        return ExifInterface.ORIENTATION_UNDEFINED;
    }

    /**
     * Returns the thumbnail for the given image url
     *
     * @param url
     * @param thumbnailSize
     * @return
     */
    public static String getThumbnail(String url, String thumbnailSize) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(thumbnailSize)) {
            Log.w(TAG, "Url or thumbnailSize is empty");
            return null;
        }

        String[] fileExtension = url.split("^(.*[\\.])");
        String[] imageUrl = url.split("\\.\\w+$");

        if (fileExtension.length > 0 && imageUrl.length > 0) {
            return imageUrl[imageUrl.length - 1] + thumbnailSize + "." + fileExtension[fileExtension.length - 1];
        }

        return null;
    }

    /**
     * Returns a drawable that has been tinted
     *
     * @param drawableId
     * @param resources
     * @param color
     * @return
     */
    public static Drawable tintDrawable(@DrawableRes int drawableId, @NonNull Resources resources, int color) {
        Drawable drawable = ResourcesCompat.getDrawable(resources, drawableId, null).mutate();
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    /**
     * Saves a bitmap to a local file
     *
     * @param bitmap
     * @return
     */
    public static File saveBitmap(@NonNull Bitmap bitmap) {
        File file = FileUtil.createFile(".jpeg");
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (Exception e) {
            e.printStackTrace();
            file = null;
        } finally {
            FileUtil.closeStream(out);
        }

        return file;
    }

    /**
     * Decodes a file path to a bitmap and returns its width and height. This <b><i>WILL NOT</i></b> load the image int memory
     *
     * @param file The file path to decode
     * @return An int array containing the width and height of the bitmap
     */
    @NonNull
    public static int[] getBitmapDimensions(@NonNull File file) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * Returns the entire size of the image cache, including videos
     *
     * @param app
     * @return
     */
    public static long getTotalImageCacheSize(MainApplication app) {
        long cacheSize = FileUtil.getDirectorySize(app.getImageLoader().getDiskCache().getDirectory());
//        cacheSize += VideoCache.getInstance().getCacheSize();
        return cacheSize;
    }

    /**
     * Checks if the user is using the new cache directory of images. If they are not, the old one will be deleted for updating.
     * <p/>
     * TODO Delete the method after several versions
     *
     * @param preferences
     * @param cacheDir
     */
    private static void checkForOldCache(@NonNull SharedPreferences preferences, @NonNull File cacheDir) {
        if (!preferences.getBoolean("has_updated_cache", false)) {
            preferences.edit().putBoolean("has_updated_cache", true).apply();

            for (File f : cacheDir.listFiles()) {
                if (!f.isDirectory()) f.delete();
            }
        }
    }
}
