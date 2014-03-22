package com.encore.views;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.TokenHelper;
import com.encore.util.T;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/5/14.
 */
public class EditProfileActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "EditProfileActivity";
    private EditText first, last, email, phone;
    private Button makeChangesBtn, logoutButton;
    private String currentFirst, currentLast, currentEmail, currentPhone;
    private ImageView profilePictureIV;
    private static File profilePic, temp;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        getViews();
        setCurrentInfo();
        setOnClickListeners();
    }

    private void getViews() {
        first = (EditText) findViewById(R.id.edit_profile_firstname);
        last = (EditText) findViewById(R.id.edit_profile_lastname);
        email = (EditText) findViewById(R.id.edit_profile_email);
        phone = (EditText) findViewById(R.id.edit_profile_phonenumber);
        makeChangesBtn = (Button) findViewById(R.id.edit_profile_submit_button);
        logoutButton = (Button) findViewById(R.id.edit_profile_logout_button);
        profilePictureIV = (ImageView) findViewById(R.id.edit_profile_picture);
    }

    private void setCurrentInfo() {
        // Pre-fill the fields with the user's current information
        Intent intent = getIntent();
        currentFirst = intent.getStringExtra(T.FIRST_NAME);
        currentLast = intent.getStringExtra(T.LAST_NAME);
        currentEmail = intent.getStringExtra(T.EMAIL);
        currentPhone = intent.getStringExtra(T.PHONE_NUMBER);

        first.setText(currentFirst);
        last.setText(currentLast);
        email.setText(currentEmail);
        phone.setText(currentPhone);

        // Set profile picture
        if (intent.getSerializableExtra(T.PROFILE_PICTURE) != null) {
            profilePic = (File)intent.getSerializableExtra(T.PROFILE_PICTURE);
            profilePictureIV.setImageURI(Uri.fromFile(profilePic));
        }
    }

    private void setOnClickListeners() {
        makeChangesBtn.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        profilePictureIV.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.edit_profile_submit_button:
                saveChanges();
                break;
            case R.id.edit_profile_logout_button:
                TokenHelper.updateToken(this, null);
                Intent introScreen = new Intent(this, WelcomeActivity.class);
                // Prevent the back button from going back to logged-in activities
                introScreen.setFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(introScreen);
                break;
            case R.id.edit_profile_picture:
                openImageIntent();  // Saves changes on result
                break;
            default:
                break;
        }
    }

    public void saveChanges() {
        Log.d(TAG, "Saving changes");
        String firstName = first.getText().toString();
        String lastName = last.getText().toString();
        String emailAddress = email.getText().toString().trim();
        String phoneNumber = PhoneNumberUtils.formatNumber(
                phone.getText().toString());

        boolean isValid =
                validateFields(firstName, lastName, emailAddress, phoneNumber);

        if(isValid) {
            updateUsersInfo(firstName, lastName, emailAddress,
                    phoneNumber, profilePic);
        }
    }

    // Make API call
    public void updateUsersInfo(String first, String last, String email,
                                String phone, File profilePicture) {
        Intent api = new Intent(this, APIService.class);
        api.putExtra(T.API_TYPE, T.UPDATE_USER);
        api.putExtra(T.FIRST_NAME, first);
        api.putExtra(T.LAST_NAME, last);
        api.putExtra(T.EMAIL, email);
        api.putExtra(T.PHONE_NUMBER, phone);
        api.putExtra(T.PROFILE_PICTURE, profilePicture);
        startService(api);

        Intent homeActivity = new Intent(this, HomeActivity.class);
        startActivity(homeActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                // Respond to the action bar's up navigation
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openImageIntent() {
        temp = T.createFile(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Rapback", "Rapback_Profile.jpg");
        Uri outputFileUri = Uri.fromFile(temp);

        // Intents that can take a picture are added to a list
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Image picker intent
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/jpeg");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, T.IMAGE_PICKER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            Log.d(TAG, "onActivityResult returned OK");
            if(requestCode == T.IMAGE_PICKER_REQUEST_CODE)
            {
                final boolean isCamera;
                if(data == null)
                {
                    isCamera = true;
                }
                else
                {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }

                Uri selectedImageUri;
                if(isCamera)
                {
                    Log.d(TAG, "profilePic from camera");
                    selectedImageUri = Uri.fromFile(temp);
                }
                else
                {
                    Log.d(TAG, "profilePic from gallery ");
                    selectedImageUri = data == null ? null : data.getData();

                    // Delete unused file
                    if(temp != null && temp.exists()) {
                        temp.delete();
                        Log.d(TAG, "Unused profile picture deleted.");
                    }
                }
                performCrop(selectedImageUri);

            }
            if(requestCode == T.CROP_IMAGE_REQUEST_CODE) {
                if(data != null) {
                    Log.d(TAG, "Cropping picture returned successfully");
                    Bundle extras = data.getExtras();
                    byte[] byteArray = extras.getByteArray("cropped-image");
                    Bitmap croppedBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    profilePictureIV.setImageBitmap(croppedBitmap);

                    profilePic = T.bitmapToFile(
                            croppedBitmap, 90, context.getCacheDir(), "Rapback_downsampled_profile");
                }
            }
        }
    }

    private void performCrop(Uri picUri) {
        Intent cropImageIntent = new Intent(this, CropImageActivity.class);
        cropImageIntent.putExtra("uri", picUri);
        startActivityForResult(cropImageIntent, T.CROP_IMAGE_REQUEST_CODE);
    }

    // Basic checking of fields
    public boolean validateFields(String firstName, String lastName, String email, String phone) {
        // First, we validate all of our fields
        String invalidMessage = "";
        if(firstName.length() <= 0) {
            invalidMessage = "Forgetting a first name?";
        } else if (lastName.length() <= 0) {
            invalidMessage = "Forgetting a last name?";
        } else if (!isValidEmail(email)) {
            invalidMessage = "Invalid email";
        } else if (!PhoneNumberUtils.isGlobalPhoneNumber(phone)) {
            invalidMessage = "Invalid phone number";
        }

        if(invalidMessage.equals("")) {
            Log.d(TAG, "All fields are valid");
            return true;
        } else {
            Toast.makeText(this, invalidMessage, Toast.LENGTH_LONG).show();
            return false;
        }
    }

    // Thank you, stackoverflow
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
