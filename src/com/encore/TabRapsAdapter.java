package com.encore;

/**
 * Created by babakpourkazemi on 3/10/14.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.encore.models.Clip;
import com.encore.widget.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by babakpourkazemi on 1/2/14.
 */
public class TabRapsAdapter extends ArrayAdapter<Clip> {
    private static final String TAG = "TabRapsAdapter";
    private Context context;
    private List<Clip> clips;
    private LayoutInflater inflater = null;
    private int layoutId;
//    private AspectRatioImageView thumbnailIv;
    private double height, width;

    // TODO: Does this send a request to /feedback on accident?
    public TabRapsAdapter(Context context, int layoutId, List<Clip> clips) {
        super(context, layoutId, clips);
        this.context = context;
        this.layoutId = layoutId;
        this.clips = clips;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layoutId, null);

            holder = new ViewHolder();
            holder.thumbnailIv = (AspectRatioImageView) convertView.findViewById(R.id.clip_thumbnail_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // And populate them
        Clip clip = clips.get(position);
        Picasso.with(context)
                .load(clip.getThumbnail_url())
                .resize((int) width,(int) height)
                .into(holder.thumbnailIv);

        return convertView;
    }

    @Override
    public int getCount() {
        if(clips != null) {
            return clips.size();
        }
        return 0;
    }

    public void setItemList(ArrayList<Clip> clips) {
        this.clips = clips;
    }

    public void setThumbnailWidth(double width) {
        this.width = width;
        this.height = 1.3*width;
    }

    static class ViewHolder {
        private AspectRatioImageView thumbnailIv;
    }
}

