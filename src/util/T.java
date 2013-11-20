package util;

public class T {
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PHONE_NUMBER = "phone_number";
	public static final String EMAIL = "email";
	public static final String ID = "id";
	public static final String API_TYPE = "api_type";
	
	public static final int NUM_VIEWS = 3;
	
	// API call types
	public static final int SIGN_IN = 0;
	public static final int SIGN_UP = 1;
	public static final int ADD_CLIP = 2;
	public static final int GET_SESSION = 5;
	public static final int GET_SESSIONS = 6;
	public static final int START_SESSION = 3;
	public static final int FRIENDS = 4;
	
	// Column names for newsfeed query
	public static final String SESSION_TITLE = "title";
	public static final String SESSION_LIKES = "num_likes";
	public static final String SESSION_SHARES = "num_shares";
	public static final String SESSION_COMMENTS = "num_comments";
	public static final String SESSION_VIDEO_URL = "video_url";
	
}
