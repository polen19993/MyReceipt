package com.polen.receipt.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.polen.receipt.R;
import com.polen.receipt.activities.MainActivity;
import com.polen.receipt.activities.MapActivity;
import com.polen.receipt.api.classes.ReceiptInfo;
import com.polen.receipt.global.Global;
import com.polen.receipt.global.GlobalFunc;
import com.polen.receipt.utils.FileUtil;
import com.polen.receipt.utils.SqlHelper;
import com.polen.receipt.utils.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class NewReceipeFragment extends BaseFragment implements View.OnClickListener {

    MainActivity mContext;
    View mRootView;

    public static EditText edtDate;
    EditText edtLocation;

    public static NewReceipeFragment newInstance(){
        return new NewReceipeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_new_receipt, container, false);
        mContext = (MainActivity)getActivity();
        initView();
        return mRootView;
    }

    private void initView(){
        ImageView imgBack = mRootView.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        edtDate = mRootView.findViewById(R.id.edt_date);
        edtDate.setOnClickListener(this);

        Button btnUpload = mRootView.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);

        Button btnShowMap = mRootView.findViewById(R.id.btn_show_map);
        btnShowMap.setOnClickListener(this);

//        Utils.printDebug(mContext, "" + Global.gCurrentLatitude);

        Button btnReport = mRootView.findViewById(R.id.btn_report);
        btnReport.setOnClickListener(this);

        edtLocation = mRootView.findViewById(R.id.edt_location);
    }

    @Override
    public void onResume() {
        super.onResume();
        edtLocation.setText("" + Global.gCurrentLatitude + ", " + Global.gCurrentLongitude);
    }

    private void insertDB(){

        File file = new File(outPath);
        if(!FileUtil.isFileValid(file)){
            Utils.printDebug(mContext, "Please Select Image!");
            return;
        }

        EditText edtTitle = mRootView.findViewById(R.id.edt_title);
        String strTitle = edtTitle.getText().toString();

        if(TextUtils.isEmpty(strTitle)){
            edtTitle.setError("please input Title");
            return;
        }

        EditText edtShopName = mRootView.findViewById(R.id.edt_shop_name);
        String strShopName = edtShopName.getText().toString();

        if(TextUtils.isEmpty(strShopName)){
            edtShopName.setError("please input Shop Name");
            return;
        }

        EditText edtComment = mRootView.findViewById(R.id.edt_comment);
        String strComment = edtComment.getText().toString();

        if(TextUtils.isEmpty(strComment)){
            edtComment.setError("please input comment");
            return;
        }

        EditText edtDate = mRootView.findViewById(R.id.edt_date);
        String strDate = edtDate.getText().toString();

        if(TextUtils.isEmpty(strDate)){
            edtDate.setError("please input date");
            return;
        }

        if(TextUtils.isEmpty(String.valueOf(Global.gCurrentLatitude))){
            Utils.printDebug(mContext, "please select location");
            return;
        }

        ReceiptInfo info = new ReceiptInfo();
        info.title = strTitle;
        info.shop_name = strShopName;
        info.comment = strComment;
        info.date = strDate;
        info.image = outPath;
        info.latitude = String.valueOf(Global.gCurrentLatitude);
        info.longitude = String.valueOf(Global.gCurrentLongitude);

        SqlHelper sqlHelper = SqlHelper.getInstance(mContext);
        sqlHelper.addReceiptData(info);


        List<ReceiptInfo> list = sqlHelper.getAllReceiptList();
        Utils.printDebug(mContext, "Report successfully");
        mContext.changeFragment(MainActivity.PAGE_HOME);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                mContext.onBackPressed();
                break;

            case R.id.btn_report:
                insertDB();
                break;

            case R.id.btn_upload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.READ_EXTERNAL_STORAGE},
                                13);
                        return;
                    }else{
                        CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(mContext, NewReceipeFragment.this);
                    }
                }
                break;

            case R.id.btn_show_map:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                10);
                        return;
                    }else{
//                      startActivityForResult(new Intent(mContext, MapActivity.class), 111);
                        mContext.changeFragment(MainActivity.PAGE_MAP);
                    }
                }
                break;

            case R.id.edt_date:
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
                break;
        }
    }



    private void startCropImage(String path) {
        Uri uri = Uri.parse(path);
        CropImage.activity(uri)
                .start(mContext);
    }

    Uri resultUri;
    String mPhotoPath = "";
    String outPath = "";
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        if (requestCode == Global.TAKE_GALLERY) {
            Uri imgUri = data.getData();
            String _strPhotoPath = Utils.getRealPathFromURI(mContext, imgUri);
            mPhotoPath = _strPhotoPath;
            startCropImage(mPhotoPath);
        } else if (requestCode == Global.TAKE_CAMERA) {
            mPhotoPath = Global.getCameraTempFilePath();
            startCropImage(mPhotoPath);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            resultUri = result.getUri();


            String strPath = resultUri.getPath();
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

            outPath = GlobalFunc.getFileFolder("photo") + "/" + ts + ".jpg";
            FileUtil.copyFile(strPath, GlobalFunc.getFileFolder("photo"),  ts + ".jpg");

            ImageView imgPhoto = mRootView.findViewById(R.id.image);
            imgPhoto.setImageURI(resultUri);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public EditText editText;
        DatePicker dpResult;

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

             edtDate.setText(String.valueOf(day) + "/"
                    + String.valueOf(month + 1) + "/" + String.valueOf(year));
        }
    }

}
