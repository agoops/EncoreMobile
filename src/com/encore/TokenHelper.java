package com.encore;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenHelper {
	private static SharedPreferences prefs;
	private static SharedPreferences.Editor editor;
	private static String TOKEN_KEY = "not set";

	public static void updateToken(Context context, String token) {
		SharedPreferences prefs = context.getSharedPreferences("com.encore",
				Context.MODE_PRIVATE);
		prefs.edit().putString(TOKEN_KEY, token).commit();
	}

	public static String getToken(Context context) {
		SharedPreferences prefs = context.getSharedPreferences("com.encore",
				Context.MODE_PRIVATE);
		return prefs.getString(TOKEN_KEY, null);
	}

}
