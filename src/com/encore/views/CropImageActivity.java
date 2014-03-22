package com.encore.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.encore.R;
import com.encore.widget.cropper.CropImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by babakpourkazemi on 3/21/14.
 */
public class CropImageActivity extends Activity implements View.OnClickListener {
    private Context context;
    private static final String TAG = "CropImageActivity";
    private Button saveButton, cancelButton;
    private ImageView rotateImageButton;
    private CropImageView cropImageView;
    private Uri selectedImageUri;
    private Bitmap bitmap;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);

        context = this;

        getViews();
        initData();
        setOnClickListeners();
    }

    private void getViews() {
        saveButton = (Button) findViewById(R.id.save_cropped_image);
        cancelButton = (Button) findViewById(R.id.cancel_cropped_image);
        rotateImageButton = (ImageView) findViewById(R.id.rotate_cropped_image);
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
    }

    private void initData() {
        try {
            rotateImageButton.setVisibility(View.GONE);

            Intent intent = getIntent();
            selectedImageUri = intent.getParcelableExtra("uri");
            cropImageView.setFixedAspectRatio(true);

            // Convert from uri to a bitmap
            // TODO: This line throws an OOM exception
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

            cropImageView.setImageBitmap(bitmap);
            rotateImageButton.setVisibility(View.VISIBLE);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnClickListeners() {
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        rotateImageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.save_cropped_image:
                Bitmap croppedImage = cropImageView.getCroppedImage();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                croppedImage.compress(Bitmap.CompressFormat.JPEG, 35, stream);
                byte[] byteArray = stream.toByteArray();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("cropped-image", byteArray);
                setResult(RESULT_OK, returnIntent);
                finish();

                break;
            case R.id.cancel_cropped_image:
                setResult(RESULT_CANCELED);
                finish();

                break;
            case R.id.rotate_cropped_image:
                cropImageView.rotateImage(90);

                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called, recycling bitmap!");
        bitmap.recycle();
    }
}
