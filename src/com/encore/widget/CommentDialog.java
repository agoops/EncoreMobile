package com.encore.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.encore.API.APIService;
import com.encore.R;
import com.encore.models.Comment;
import com.encore.util.T;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommentDialog extends Dialog implements OnClickListener {
	private static final String TAG = "CommentDialog";
	private ListView commentLv;
    private EditText postCommentEt;
    private Button postCommentBtn;
    private Dialog dialog;

    private Context context;
    private int sessionId;
	private List<Comment> comments;
	
	public CommentDialog(Context c) {
		super(c);
		this.context = c;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 1. Set the dialog's settings
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment_dialog);

		// 2. Get our views
		commentLv = (ListView) findViewById(R.id.comment_listview);
		postCommentEt = (EditText) findViewById(R.id.comment_edittext);
		postCommentBtn = (Button) findViewById(R.id.comment_post_button);

		postCommentBtn.setOnClickListener(this);

		// 3. Finally attach the comments
		CommentsAdapter adapter = new CommentsAdapter(context, R.layout.comment_list_row, comments);
		commentLv.setAdapter(adapter);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.comment_post_button:
			Log.d(TAG, "POSTing comment to session id: " + sessionId);
			Intent api = new Intent(context, APIService.class);
			api.putExtra(T.API_TYPE, T.CREATE_COMMENT);
			api.putExtra(T.SESSION_ID, sessionId);
			api.putExtra(T.COMMENT_TEXT, postCommentEt.getText().toString());
			context.startService(api); 
			break;
		default:
			break;
		}
		dismiss();
	}

    // Useful for passing the comments data in from another class
	public void setDialogArgs(Bundle args) {
		this.sessionId = args.getInt("sessionId");
		String commentsJson = args.getString("comments");
		Log.d(TAG, "Received comments: " + commentsJson);
		
		Type listType = new TypeToken<ArrayList<Comment>>() {}.getType();
		comments = new Gson().fromJson(commentsJson, listType);
	}
}
