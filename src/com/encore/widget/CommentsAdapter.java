package com.encore.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.R;
import com.encore.models.Comment;

import java.util.List;

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
        ViewHolder holder;

        // Get the view elements
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.comment_list_row, parent, false);

            holder = new ViewHolder();
			holder.creator = (TextView) convertView.findViewById(R.id.commentCreator);
            holder.comment = (TextView) convertView.findViewById(R.id.commentText);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		// And set the creator and comment
		Comment comment = comments.get(position);
        holder.creator.setText(comment.getCommenter());
        holder.comment.setText(comment.getText());
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView creator, comment;
	}
}
