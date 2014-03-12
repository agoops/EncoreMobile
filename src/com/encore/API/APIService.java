package com.encore.API;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.encore.TokenHelper;
import com.encore.models.Feedback;
import com.encore.models.PostComment;
import com.encore.models.PostLike;
import com.encore.util.T;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.io.File;

public class APIService extends IntentService {
	public static String TAG = "APIService";
	API api;
	ResultReceiver resultReceiver = null;

	public APIService() {
		super("APIService");
		Log.d(TAG, "APIService constructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling intent type " + intent.getIntExtra(T.API_TYPE, -1));
		
		if (intent.hasExtra(T.RECEIVER)) {
			Log.d(TAG, "Receiver received");
			resultReceiver = intent.getParcelableExtra("receiver");
		} else {
			Log.d(TAG, "No receiver received");
		}
		
		api = new API(new OkHttpClient(), this);
		// Where processing occurs
		int apiType = intent.getIntExtra(T.API_TYPE, -1);
		switch (apiType)
        {
            case T.SIGN_IN:
                login(intent.getExtras());
                break;
            case T.SIGN_UP:
                signUp(intent.getExtras());
                break;
            case T.CREATE_SESSION:
                createSession(intent.getExtras());
                break;
            case T.GET_FRIENDS:
                getFriends(intent.getExtras());
                break;
            case T.FRIEND_REQUEST:
                sendFriendRequest(intent.getExtras());
                break;
            case T.FRIEND_REQUESTS_PENDING:
                getPendingFriendRequests(intent.getExtras());
                break;
            case T.USERS:
                getUsers(intent.getExtras());
                break;
            case T.ADD_CLIP:
                Log.d(TAG, "case ADD_CLIP in API Service");
                addClip(intent.getExtras());
                break;
            case T.ACCEPT_FRIEND_REQUEST:
                acceptFriendRequest(intent.getExtras());
                break;
            case T.GET_LIVE_SESSIONS:
                getLiveSessions();
                break;
            case T.GET_COMPLETE_SESSIONS:
                getCompleteSessions();
                break;
            case T.CREATE_COMMENT:
                createComment(intent.getExtras());
                break;
            case T.CREATE_LIKE:
                createLike(intent.getExtras());
                break;
            case T.GET_CLIP_STREAM:
                getClipStream(intent.getExtras());
                break;
            case T.GET_ME:
                getMe(intent.getExtras());
                break;
            case T.GET_LIKES:
                getLikes(intent.getExtras());
                break;
            case T.UPDATE_USER:
                updateUser(intent.getExtras());
                break;
            case T.SEARCH_USERNAME:
                searchUsername(intent.getExtras());
                break;
            case T.PAGINATE_NEXT_SESSION:
                paginateNextSession(intent.getExtras());
                break;
            case T.GET_OTHER_PROFILE:
                getOtherProfile(intent.getExtras());
                break;
            case T.GET_CLIPS:
                getClips(intent.getExtras());
            case T.FEEDBACK:
                sendFeedback(intent.getExtras());
                break;
            default:
                break;
		}
	}
	
	
	private void acceptFriendRequest(Bundle data) {
		String username = data.getString(T.USERNAME);
		boolean accepted = data.getBoolean(T.ACCEPTED);
		Log.d(TAG, "Value of accepted in apiservice: " + accepted);
		JSONObject json = null;
		StringEntity entity = null;
		
		try {
			json  = new JSONObject();
			json.put(T.USERNAME, username);
			json.put(T.ACCEPTED, accepted);
			entity = new StringEntity(json.toString());
			String result = api.acceptFriendRequest(entity);
			Log.d(TAG, result);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void addClip(Bundle data) {
		int sessionId = data.getInt(T.SESSION_ID);
		MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
				.create();
		multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		multipartEntity.setBoundary("---*******");
		multipartEntity.addPart("clip", new FileBody(new File(data.getString(T.FILEPATH))));
		multipartEntity.addPart("thumbnail", new FileBody(new File(data.getString(T.THUMBNAIL_FILEPATH))));
		HttpEntity entity = multipartEntity.build();
		try {
			String result = api.addClip(entity, sessionId);
			Log.d(TAG, "FROM addClip() apiservice" + result);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getPendingFriendRequests(Bundle data) {
		String token = TokenHelper.getToken(this);
		try{
			String result = api.getPendingFriendRequests(token);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void sendFriendRequest(Bundle data) {
		String token = TokenHelper.getToken(this);
		
		String username = data.getString(T.USERNAME);
		JSONObject json = null;
		StringEntity entity = null;
		/**
		 * TODO: 
		 * problem with POST body (maybe not right username) is somewhere here.
		 */
		try{
			json = new JSONObject();
			json.put(T.USERNAME, username);
			Log.d(TAG, "json for entity: " + json.toString());
			entity = new StringEntity(json.toString());
			Log.d(TAG, entity.toString());
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
		try {
			String result = api.sendFriendRequest(token, entity);
			Log.d(TAG, "response from sendFriendRequest: " + result);
		} catch (Exception e ){
			e.printStackTrace();
		}
	}
	
	
	private void getFriends(Bundle data) {
		Log.d(TAG, "getFriends called");
		String token = TokenHelper.getToken(this);
		Log.d(TAG, "token: " + token);
		if (token == null) {
			Log.d(TAG, "No token in shared prefs");
		}
		try {
			String result = api.getFriends(token);
			Log.d(TAG, "result from api: " + result);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void getUsers(Bundle data) {
		Log.d(TAG, "getUsers called");
		String token = TokenHelper.getToken(this);
		Log.d(TAG, "token: " + token);
		if (token == null) {
			Log.d(TAG, "No token in shared prefs");
		}
		try {
			String result = api.getUsers(token);
			Log.d(TAG, "result from api: " + result);
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login(Bundle data) {
		Log.d(TAG, "login called");
        String username = data.getString(T.USERNAME);
        String password = data.getString(T.PASSWORD);

        try {
			JSONObject json = new JSONObject();
			json.put(T.USERNAME, username);
			json.put(T.PASSWORD, password);
			StringEntity entity = new StringEntity(json.toString());

			String result = api.login(entity);
			String token = (new JSONObject(result)).getString("token");
			TokenHelper.updateToken(this, token);

			Log.d(TAG, "result from api: " + result);
			resultReceiver.send(1, new Bundle());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
			e.printStackTrace();
			resultReceiver.send(0, null);
		}

	}

	private void signUp(Bundle data) {
		Log.d(TAG, "signUp called");
		String result = "FAILED";
        String username = data.getString(T.USERNAME);
        String password = data.getString(T.PASSWORD);
        String firstName = data.getString(T.FIRST_NAME);
        String lastName = data.getString(T.LAST_NAME);
        String email = data.getString(T.EMAIL);
        String phoneNumber = data.getString(T.PHONE_NUMBER);


        // TODO: Add some sort of profile picture flow

		try {
			JSONObject json = new JSONObject();
            json.put(T.USERNAME, username);
            json.put(T.PASSWORD, password);
            json.put(T.FIRST_NAME, firstName);
            json.put(T.LAST_NAME, lastName);
            json.put(T.EMAIL, email);
            json.put(T.PHONE_NUMBER, phoneNumber);
            json.put(T.PROFILE_PICTURE, null);

			StringEntity entity = new StringEntity(json.toString());
			result = api.signUp(entity);
			String token = (new JSONObject(result)).getString("token");
			
			TokenHelper.updateToken(this, token);
			Bundle bundle = new Bundle();
			bundle.putString("token", token);
			resultReceiver.send(1, bundle);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
			resultReceiver.send(0,null);
		}
		
	}

	private void createSession(Bundle data) {
        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        multipartEntity.setBoundary("---*******");
        multipartEntity.addTextBody("title", data.getString(T.SESSION_TITLE));
        multipartEntity.addPart("clip", new FileBody(new File(data.getString(T.FILEPATH))));
        multipartEntity.addPart("thumbnail", new FileBody(new File(data.getString(T.THUMBNAIL_FILEPATH))));

	    HttpEntity entity = multipartEntity.build();
		try {
			String result = api.createSession(entity);
			Log.d(TAG, "FROM createSessions() apiservice"+result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getLiveSessions() {
		Log.d(TAG, "getLiveSessions called");

		try {
			String result = api.getLiveSessions();
			Bundle b = new Bundle();
			b.putString("result", result);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			e.printStackTrace();
			resultReceiver.send(0,null);
		}
	}

    private void getCompleteSessions() {
        Log.d(TAG, "getCompleteSessions called");

        try {
            String result = api.getCompleteSessions();
            Bundle b = new Bundle();
            b.putString("result", result);
            resultReceiver.send(1, b);
        } catch (Exception e) {
            e.printStackTrace();
            resultReceiver.send(0,null);
        }
    }
	
	private void createComment(Bundle data) {
		Log.d(TAG, "createComment called");
		
		int sessionId = data.getInt(T.SESSION_ID);
		String commentText = data.getString(T.COMMENT_TEXT);
		String resultJSON = null;
		
		PostComment pComment = new PostComment(sessionId, commentText);
		try {
			resultJSON = api.createComment(pComment, TokenHelper.getToken(this), sessionId);
			Log.d(TAG, "createComment result: " + resultJSON);
			
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}
	
	private void createLike(Bundle data) {
		Log.d(TAG, "createLike called");
		int sessionId = data.getInt(T.SESSION_ID);
		String token = TokenHelper.getToken(this);
		String resultJSON = null;
		
		try {
			PostLike like = new PostLike(sessionId);
			resultJSON = api.createLike(like, token);
			Log.d(TAG, "createLike result: " + resultJSON);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
		}
	}
	
	private void getClipStream(Bundle data) {
		Log.d(TAG, "getClipStream called");
		int sessionId = data.getInt(T.SESSION_ID);
		String resultJSON = null;
		
		try {
			resultJSON = api.getClipStream(sessionId);
			Log.d(TAG, "getClipStream result: " + resultJSON);
			
			Bundle b = new Bundle();
			b.putString("result", resultJSON);
			resultReceiver.send(1, b);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage() + " ");
			e.printStackTrace();
			resultReceiver.send(0, null);
		}
	}
	
	private void getMe(Bundle data) {
		Log.d(TAG, "getMe called");
		String token = TokenHelper.getToken(this);
		String resultJSON = null;
		
		try {
			resultJSON = api.getMe(token);
			
			Bundle b = new Bundle();
			b.putString("result", resultJSON);
			resultReceiver.send(1, b);
		} catch(Exception e) {
			Log.e(TAG, e.getMessage() + " ");
			e.printStackTrace();
			resultReceiver.send(0, null);
		}
	}

    private void getLikes(Bundle data) {
        Log.d(TAG, "getLikes called");
        String token = TokenHelper.getToken(this);
        String resultJSON = null;

        try {
            resultJSON = api.getLikes(token);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }

    private void getClips(Bundle data) {
        Log.d(TAG, "getClips called");
        String token = TokenHelper.getToken(this);
        String resultJSON = null;

        try {
            resultJSON = api.getClips(token);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }

    private void updateUser(Bundle data) {
        Log.d(TAG, "updateUser called");
        String token = TokenHelper.getToken(this);
        String firstName = data.getString(T.FIRST_NAME);
        String lastName = data.getString(T.LAST_NAME);
        String email = data.getString(T.EMAIL);
        String phoneNumber = data.getString(T.PHONE_NUMBER);
        File profilePicture = (File)data.getSerializable(T.PROFILE_PICTURE);

        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder
                .create();
        multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        multipartEntity.setBoundary("---*******");
        multipartEntity.addTextBody(T.FIRST_NAME, firstName);
        multipartEntity.addTextBody(T.LAST_NAME, lastName);
        multipartEntity.addTextBody(T.EMAIL, email);
        multipartEntity.addTextBody(T.PHONE_NUMBER, phoneNumber);
        multipartEntity.addPart(T.PROFILE_PICTURE, new FileBody(profilePicture));
        HttpEntity entity = multipartEntity.build();

        try {
            String resultJSON = api.updateUser(token, entity);
            Log.d(TAG, "updateUser result: " + resultJSON);

        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
        }
    }

    private void searchUsername(Bundle data) {
        Log.d(TAG, "searchUsername called");
        String token = TokenHelper.getToken(this);
        String username = data.getString(T.USERNAME);

        String resultJSON = null;

        try {
            resultJSON = api.searchUsername(token, username);
            Log.d(TAG, "resultJSON result: " + resultJSON);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }

    private void paginateNextSession(Bundle data) {
        Log.d(TAG, "paginateNextSession called");
        String token = TokenHelper.getToken(this);

        // The URL must be toString()'d from a paginator
        String nextSessionUrl = data.getString(T.NEXT_SESSION_URL);

        String resultJSON = null;

        try {
            resultJSON = api.paginateNextSession(token, nextSessionUrl);
            Log.d(TAG, "resultJSON result: " + resultJSON);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }

    private void getOtherProfile(Bundle data) {
        Log.d(TAG, "getOtherProfile called");
        String username = data.getString(T.USERNAME);
        String token = TokenHelper.getToken(this);
        String resultJSON = null;

        try {
            resultJSON = api.getOtherProfile(token, username);
            Log.d(TAG, "result: " + resultJSON);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }

    private void sendFeedback(Bundle data) {
        Log.d(TAG, "sendFeedback called");
        String feedbackString = data.getString(T.FEEDBACK_KEY);
        String token = TokenHelper.getToken(this);
        String resultJSON;
        Feedback feedback = new Feedback(feedbackString);

        try {
            resultJSON = api.sendFeedback(token, feedback);
            Log.d(TAG, "result: " + resultJSON);

            Bundle b = new Bundle();
            b.putString("result", resultJSON);
            resultReceiver.send(1, b);
        } catch(Exception e) {
            Log.e(TAG, e.getMessage() + " ");
            e.printStackTrace();
            resultReceiver.send(0, null);
        }
    }
}
