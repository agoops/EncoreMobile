package widget;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.encore.R;
import com.encore.API.models.Session;

public class SessionsAdapter extends ArrayAdapter<Session> {
	private Context context;
    private List<Session> sessions;
    private static LayoutInflater inflater = null;

    // Used to populate our newsfeed with a list of sessions
    public SessionsAdapter (Context context, int textViewResourceId, List<Session> sessions) {
        super(context, textViewResourceId, sessions);
        this.context = context;
        this.sessions = sessions;
    }
    
    // Returns the view, with appropriate data, to our custom listview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View rowView = convertView;
    	final ViewHolder viewHolder;
    	
		if (convertView == null) {
			rowView = inflater.inflate(R.layout.newsfeed_list_row, null);
			viewHolder = new ViewHolder();
			
			viewHolder.newsfeed_session_title = (TextView) rowView.findViewById(R.id.newsfeed_session_title);
			viewHolder.newsfeed_session_video = (TextView) rowView.findViewById(R.id.newsfeed_session_video);
			viewHolder.newsfeed_session_likes = (TextView) rowView.findViewById(R.id.newsfeed_session_likes);
			viewHolder.newsfeed_session_comments = (TextView) rowView.findViewById(R.id.newsfeed_session_comments);
			viewHolder.newsfeed_session_shares = (TextView) rowView.findViewById(R.id.newsfeed_session_shares);
			
			rowView.setTag(viewHolder);
		} else {
			// Avoids calling findViewById() on resource every time.
			viewHolder = (ViewHolder) rowView.getTag();
		}
		
		// Fill each row with the session at "position" 
		viewHolder.newsfeed_session_title.setText(sessions.get(position).getTitle());
		viewHolder.newsfeed_session_video.setText(sessions.get(position).getVideo_url());
		// "Here" is not being called. This is our problem!!
//		viewHolder.newsfeed_session_likes.setText(sessions.get(position).getNum_likes());
		viewHolder.newsfeed_session_comments.setText(sessions.get(position).getNum_comments());
		Log.d("SessionsAdapter", "Here");
//		viewHolder.newsfeed_session_shares.setText(sessions.get(position).getNum_shares());
		
    	return rowView;
    }
    public int getCount() {
        return sessions.size();
    }

    public static class ViewHolder {
        public TextView newsfeed_session_title, newsfeed_session_video, 
        	newsfeed_session_likes, newsfeed_session_comments, 
        	newsfeed_session_shares;
    }
}
