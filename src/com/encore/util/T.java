package com.encore.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class T {
    public static final String TAG = "T";

	public static final String USERNAME = "username";
    public static final String MY_USERNAME = "my_username";
	public static final String PASSWORD = "password";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String EMAIL = "email";
	public static final String ID = "id";
	public static final String API_TYPE = "api_type";
	public static final String RECEIVER = "receiver";
	public static final String SESSION = "session";
	public static final String DURATION = "duration";
	public static final String FILEPATH = "filepath";
	public static final String ACCEPTED = "accepted";
	public static final String THUMBNAIL_FILEPATH = "thumbnail_filepath";
    public static final String NEXT_SESSION_URL= "next_session_url";
    public static final String PREVIOUS_SESSION_URL = "previous_session_url";
    public static final String PENDING_THEM = "pending_them";
    public static final String CLIP_URL = "clip_url";
    public static final String ALL_CLIPS = "all_clips";
    public static final String IS_COMPLETE = "is_complete";
    public static final String PROFILE_PICTURE = "profile_picture";
    public static final String PROFILE_INFO_TYPE = "profile_info_type";
    public static final String PROFILE_INFO_RAPS = "My Raps";
    public static final String PROFILE_INFO_LIKES = "My Likes";
    public static final String PROFILE_INFO_FRIENDS = "My Friends";
    public static final String FEEDBACK_KEY = "feedback";
    public static final String TEXT = "text";


	// API call types
	public static final int SIGN_IN = 0;
	public static final int SIGN_UP = 1;
	public static final int ADD_CLIP = 2;
	public static final int CREATE_SESSION = 3;
	public static final int GET_FRIENDS = 4;
	public static final int USERS = 5;
	public static final int FRIEND_REQUEST = 6;
	public static final int FRIEND_REQUESTS_PENDING = 9;
	public static final int ACCEPT_FRIEND_REQUEST = 10;
	public static final int GET_LIVE_SESSIONS = 11;
    public static final int GET_COMPLETE_SESSIONS = 12;
	public static final int CREATE_COMMENT = 13;
	public static final int CREATE_LIKE = 14;
	public static final int GET_ME = 15;
	public static final int GET_CLIP_STREAM = 16;
    public static final int GET_LIKES = 17;
    public static final int UPDATE_USER = 18;
    public static final int SEARCH_USERNAME = 19;
    public static final int PAGINATE_NEXT_SESSION = 20;
    public static final int GET_OTHER_PROFILE = 21;
    public static final int GET_CLIPS = 22;
    public static final int FEEDBACK = 23;
    public static final int SEND_INVITE = 24;
	
	// Column names for newsfeed query
	public static final String SESSION_TITLE = "title";
	public static final String SESSION_ID = "id";

	public static final String COMMENT_TEXT = "comment_text";

    // For determining SessionFlowManager's flow
    public static final String FEED_TYPE = "feed_type";
    public static final int LIVE_FEED = 1;
    public static final int COMPLETE_FEED = 2;

    // Request codes
    public static final int IMAGE_PICKER_REQUEST_CODE = 100;

    public static void setTypeFace(Context c, TextView... views) {
        Typeface typeface = Typeface.createFromAsset(c.getAssets(),
                "Raleway-Bold.ttf");
        for(TextView view : views) {
            view.setTypeface(typeface);
        }
    }

    /*
     *   Creates a new file in the given directory.
     *   If the directory doesn't exist, it is created.
     *   Returns the new file.
     */
    public static File createFile(File dir, String dirName, String fileName){
        // TODO: Check the SDCard is mounted using Environment.getExternalStorageState()

        File mediaStorageDir = new File(dir, dirName);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            Log.d(TAG, "The following directory doesn't exist: " + mediaStorageDir);
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        String path;
        path = mediaStorageDir.getPath() + File.separator +
                fileName;
        File newFile = new File(path);

        try {
            if(newFile.exists()) {
                newFile.delete();
            }
            newFile.createNewFile();
        } catch (IOException e) {
            Log.d(TAG, "Failed to create new file");
            e.printStackTrace();
        }
        return newFile;
    }

    /*
     *   -------------- Bitmap manipulation ----------------
     */

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static File bitmapToFile(Bitmap bitmap, int compressionQuality, File dir, String fileName) {
        // Read the bitmap into a file
        File f = new File(dir, fileName);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    /*
      * Downsamples the given image to prevent out-of-memory errors
      */
    public static Bitmap decodeUri(Context context, Uri selectedImageUri, String photoPath) {
        try {
            // TODO: Crop the image. Better yet, let the user crop the image.
            final int MAX_IMAGE_DIMENSION = 140;

            InputStream is = context.getContentResolver().openInputStream(selectedImageUri);
            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, dbo);
            is.close();

            int rotatedWidth, rotatedHeight;
            int orientation = getOrientation(context, selectedImageUri, photoPath);

            if (orientation == 90 || orientation == 270) {
                rotatedWidth = dbo.outHeight;
                rotatedHeight = dbo.outWidth;
            } else {
                rotatedWidth = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            }

            Bitmap srcBitmap;
            is = context.getContentResolver().openInputStream(selectedImageUri);
            if (rotatedWidth > MAX_IMAGE_DIMENSION || rotatedHeight > MAX_IMAGE_DIMENSION) {
                float widthRatio = ((float) rotatedWidth) / ((float) MAX_IMAGE_DIMENSION);
                float heightRatio = ((float) rotatedHeight) / ((float) MAX_IMAGE_DIMENSION);
                float maxRatio = Math.max(widthRatio, heightRatio);

                // Create the bitmap from file
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = (int) maxRatio;
                srcBitmap = BitmapFactory.decodeStream(is, null, options);
            } else {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            is.close();
            /*
             * if the orientation is not 0 (or -1, which means we don't know), we
             * have to do a rotation.
             */
            if (orientation > 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }

            return srcBitmap;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    /*
        Get the orientation information of a photo.
     */
    public static int getOrientation(Context context, Uri photoUri, String photoPath) {
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

        if(cursor == null) {
            try {
                ExifInterface exif = new ExifInterface("Rapback_Profile");
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d(TAG, "orientation: " + orientation);
                switch(orientation) {
                    case 0:
                        // Case 0 can be either portrait or landscape
                        // AKA this doesn't always work...
                        return 270;
                    case 1:
                        return 0;
                    case 3:
                        return 180;
                    case 6:
                        return 270;
                    case 8:
                        return 90;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, "cursor: " + cursor);

        if (cursor == null ||
                cursor.getCount() != 1) {
            return -1;
        }

        cursor.moveToFirst();
        return cursor.getInt(0);
    }
}
