package com.polen.receipt.dlgs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ProgressBar;

import com.polen.receipt.R;
import com.polen.receipt.views.imagelist.ImageLoader;
import com.polen.receipt.views.ViewTouchImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;


@SuppressLint({ "ResourceAsColor", "ClickableViewAccessibility" })
public class FullPhotoDialog extends Dialog {
	
	Context context;
	ViewTouchImage imgPhoto;
	String photoUrl;
	ProgressBar progress;
	ImageLoader imageLoader;
	
	public FullPhotoDialog(Context ctx, String url){
		super(ctx, android.R.style.Theme_Translucent_NoTitleBar); 
		context = ctx;
		this.photoUrl = url;
	}
	
	@Override
    public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState ) ;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setBackgroundDrawable(new ColorDrawable(0xCC000000));
        
        setContentView( R.layout.dlg_full_photo);
        
        //setCancelable(true);
        progress = (ProgressBar) findViewById(R.id.progress);
        imgPhoto = (ViewTouchImage) findViewById(R.id.imgPhoto);
        
        imageLoader = new ImageLoader(context);
//		imageLoader.DisplayImage(GlobalConstant.SERVER_URL + photoUrl, imgPhoto, progress);
//		imageLoader.DisplayImage(photoUrl, imgPhoto, progress);
		Picasso.with(context).load(Uri.fromFile(new File(photoUrl))).into(imgPhoto, new Callback() {
			@Override
			public void onSuccess() {

			}

			@Override
			public void onError() {

			}
		});
//
//       findViewById(R.id.layoutBg).setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				dismiss();
//				return false;
//			}
//		});
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
	    Rect dialogBounds = new Rect();
	    getWindow().getDecorView().getHitRect(dialogBounds);

//	    if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
	        // Tapped outside so we finish the activity
	        dismiss();
//	    }
	    return super.dispatchTouchEvent(ev);
	}
}
