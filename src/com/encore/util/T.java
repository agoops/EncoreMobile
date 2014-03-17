package com.encore.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

import com.encore.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static final String THUMBNAIL_KEY = "thumbnail";
    public static final String NEXT_SESSION_URL= "next_session_url";
    public static final String PREVIOUS_SESSION_URL = "previous_session_url";
    public static final String PENDING_THEM = "pending_them";
    public static final String CLIP_KEY = "clip";

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
    public static final String IS_BATTLE = "is_battle";
    public static final String SESSION_CREATOR = "session_creator";
    public static final String BATTLE_RECEIVER = "battle_receiver";


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

	// Column names for newsfeed query
	public static final String SESSION_TITLE = "title";
	public static final String SESSION_ID = "id";

	public static final String COMMENT_TEXT = "comment_text";

    // Request codes
    public static final int IMAGE_PICKER_REQUEST_CODE = 100;
    public static final int CONTACT_NUMBER_REQUEST_CODE = 200;

    // Arc menu drawables
    public static final int[] ITEM_DRAWABLES = { R.drawable.bg_new_session_btn, R.drawable.bg_new_battle };

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

    public static File bitmapToFile(Bitmap bitmap, int compressionQuality, File dir, String fileName) {
        // Read the bitmap into a file
        File f = new File(dir, fileName);
        if(f.exists()) {
            f.delete();
//            f = new File(dir, fileName);
            try {
                f.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
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
}
