package widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.encore.R;
import com.encore.API.models.Crowd;

public class CrowdAdapter extends ArrayAdapter<Crowd> implements OnCheckedChangeListener {
	private static final String TAG = "CrowdAdapter";
	private Context context;
	private List<Crowd> crowds;
	private List<Crowd> selectedCrowds;
	private static LayoutInflater inflater = null;
	private int numCrowdsSelected;
	
	public CrowdAdapter(Context context, int textViewResourceId, List<Crowd> crowds) {
		super(context, textViewResourceId, crowds);
		this.context = context;
		this.crowds = crowds;
		selectedCrowds = new ArrayList<Crowd>(crowds.size());
		numCrowdsSelected = 0;
	}
	
	// Returns the appropriate view to our custom listview
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View rowView = convertView;
    	final CrowdHolder viewHolder;
    	
    	// Get the crowd_list_row
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.crowd_list_row, null);
			viewHolder = new CrowdHolder();
			
			viewHolder.crowdTitle = (TextView) rowView.findViewById(R.id.crowd_title);
			viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.crowd_checkbox);
			
			rowView.setTag(viewHolder);
		} else {
			// Avoids calling findViewById() on resource every time.
			viewHolder = (CrowdHolder) rowView.getTag();
		}
		
		// And update its title (which will happen for all crowds)
		Crowd crowd = crowds.get(position);
		viewHolder.crowdTitle.setText(crowd.getTitle());
		viewHolder.checkBox.setTag(position);
		viewHolder.checkBox.setChecked(false);
		viewHolder.checkBox.setOnCheckedChangeListener(this);
		
    	return rowView;
	}
	
	@Override
	public void onCheckedChanged(CompoundButton checkboxView,
			boolean isChecked) {
		int position = (Integer) checkboxView.getTag();
		if(isChecked) {
			if(numCrowdsSelected > 0) {
				Toast.makeText(context, "You can only pick 1 crowd (for now!)", Toast.LENGTH_SHORT).show();
				checkboxView.setChecked(false);
			} else {
				Log.d("CrowdAdapter", "Checked " + (Integer)checkboxView.getTag());
				selectedCrowds.add(crowds.get(position));
				numCrowdsSelected++;
			}
		} else {
			selectedCrowds.remove(crowds.get(position));
			numCrowdsSelected--;
		}
	}
	
	public List<Crowd> getSelectedCrowds() {
		return selectedCrowds;
	}
	
	static class CrowdHolder {
		TextView crowdTitle;
		CheckBox checkBox;
	}
}
