package widget;

import java.util.List;

import widget.SessionsAdapter.ViewHolder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.encore.R;
import com.encore.API.models.Crowd;

public class CrowdAdapter extends ArrayAdapter<Crowd> {
	private Context context;
	private List<Crowd> crowds;
	private static LayoutInflater inflater = null;
	
	// Use to populate our newsfeed with a list of crowds
	public CrowdAdapter(Context context, int textViewResourceId, List<Crowd> crowds) {
		super(context, textViewResourceId, crowds);
		this.context = context;
		this.crowds = crowds;
	}
	
	// Returns the appropriate view to our custom listview
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View rowView = convertView;
    	final CrowdHolder viewHolder;
    	
    	// Get the create_crowd_list_row.xml
		if (convertView == null) {
			rowView = inflater.inflate(R.layout.create_crowd_list_row, null);
			viewHolder = new CrowdHolder();
			
			viewHolder.crowdTitle = (TextView) rowView.findViewById(R.id.crowd_title);
			
			rowView.setTag(viewHolder);
		} else {
			// Avoids calling findViewById() on resource every time.
			viewHolder = (CrowdHolder) rowView.getTag();
		}
		
		// And update its title (which will happen for all crowds)
		Crowd crowd = crowds.get(position);
		viewHolder.crowdTitle.setText(crowd.getTitle());
		
    	return rowView;
	}
	
	static class CrowdHolder {
		TextView crowdTitle;
	}
}
