package com.encore.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.R;
import com.encore.models.Comment;

public class CommentsAdapter extends ArrayAdapter<Comment> {
	private Context context;
	private List<Comment> comments;
	private LayoutInflater inflater;
	
	public CommentsAdapter(Context context, int textViewResourceId, List<Comment> comments) {
		super(context, textViewResourceId, comments);
		this.context = context;
		this.comments = comments;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = convertView;
		final CommentsHolder viewHolder;
		
		// Get the view elements
		if(rowView == null) {
			rowView = inflater.inflate(R.layout.comment_list_row, parent, false);
			viewHolder = new CommentsHolder();
			
			viewHolder.creator = (TextView) rowView.findViewById(R.id.commentCreator);
			viewHolder.comment = (TextView) rowView.findViewById(R.id.commentText);
			
			rowView.setTag(viewHolder);
		} else {
			viewHolder = (CommentsHolder) rowView.getTag();
		}
		
		// And set the creator and comment
		Comment comment = comments.get(position);
		viewHolder.creator.setText(Integer.toString(comment.getCreator()));
		viewHolder.comment.setText(comment.getText());
		
		return rowView;
	}
	
	static class CommentsHolder {
		TextView creator, comment;
	}
}
