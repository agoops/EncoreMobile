package com.encore.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.encore.R;
import com.encore.util.T;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import java.io.File;
import java.util.HashMap;

/**
 * Created by babakpourkazemi on 3/17/14.
 */
public class ImageLoaderWrapper {
    private ImageLoader imageLoader;
    private Context context;
    public HashMap<String, ImageView> uriToImageView;
    private int count;

    public ImageLoaderWrapper(Context context) {
        this.context = context;
    }

    public void init() {
        this.imageLoader = ImageLoader.getInstance();
        this.uriToImageView = new HashMap<String, ImageView>();
    }

    public void loadImage(String url) {
        imageLoader.loadImage(url, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                ImageView thumbnail = uriToImageView.get(imageUri);
                thumbnail.setImageDrawable(
                        context.getResources().getDrawable(R.drawable.background_333_transparent2));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                String filename = "Rapback_thumbnail_" + count;
                count += 1;
                File f = T.bitmapToFile(loadedBitmap, 90,
                        context.getCacheDir(), filename);

                ImageView thumbnail = uriToImageView.get(imageUri);
                thumbnail.setImageURI(null);
                thumbnail.setImageURI(Uri.fromFile(f));
            }
        });
    }
}
