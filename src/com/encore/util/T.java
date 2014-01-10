package com.encore.util;

public class T {
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

	// API call types
	public static final int SIGN_IN = 0;
	public static final int SIGN_UP = 1;
	public static final int ADD_CLIP = 2;
	public static final int CREATE_SESSION = 3;
	public static final int GET_FRIENDS = 4;
	public static final int USERS = 5;
	public static final int FRIEND_REQUEST = 6;
	public static final int GET_CROWDS = 7;
	public static final int CREATE_CROWD = 8;
	public static final int FRIEND_REQUESTS_PENDING = 9;
	public static final int ACCEPT_FRIEND_REQUEST = 10;
	public static final int GET_SESSIONS = 11;
	public static final int CREATE_COMMENT = 12;
	public static final int CREATE_LIKE = 13;
	public static final int GET_ME = 14;
	public static final int GET_CLIP_STREAM = 15;
    public static final int GET_LIKES = 16;
    public static final int UPDATE_USER = 17;
    public static final int SEARCH_USERNAME = 18;
    public static final int PAGINATE_NEXT_SESSION = 19;
    public static final int GET_OTHER_PROFILE = 20;
	
	// Column names for newsfeed query
	public static final String SESSION_TITLE = "title";
	public static final String SESSION_ID = "id";
	public static final String SESSION_USE_EXISTING_CROWD = "use_existing_crowd";
	public static final String SESSION_CROWD_TITLE = "crowd_title";
	public static final String SESSION_CROWD_MEMBERS = "crowd_members";
	public static final String SESSION_CROWD_ID = "crowd";
	
	public static final String CROWD_TITLE = "title";
	public static final String CROWD_MEMBERS = "members";
	
	public static final String COMMENT_TEXT = "comment_text";
}
