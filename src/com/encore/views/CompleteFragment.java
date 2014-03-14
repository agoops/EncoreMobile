package com.encore.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.models.Paginator;
import com.encore.models.Session;
import com.encore.util.T;
import com.encore.widget.ArcMenu;
import com.encore.widget.EndlessScrollListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by babakpourkazemi on 3/5/14.
 */
public class CompleteFragment extends Fragment implements OnRefreshListener {
    private static final String TAG = "LiveFragment";
    private Context context;
    private VideoView videoView;
    private InboxViewAdapter adapter;
    private Session[] sessions;
    private ListView listView;
    private ProgressBar progressBar;
    private static ResultReceiver receiver;
    private PullToRefreshLayout pullToRefreshLayout;
    private Paginator paginator;
    private ArcMenu arcMenu;

    private boolean flagLoading = false;
    private List<Session> sessionsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.video_list_fragment, container, false);
        context = getActivity();

        initData();
        getViews(view);
        setupArcMenu(T.ITEM_DRAWABLES, arcMenu);
        setupListView(view);

        // TODO: set click listener on the comments+likes container

        return view;
    }

    private void initData() {
        sessionsList = new ArrayList<Session>();
    }

    private void getViews(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_live);
        listView = (ListView) view.findViewById(R.id.video_list_view2);
        arcMenu = (ArcMenu) view.findViewById(R.id.arc_menu);

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setupArcMenu(int[] item_drawables, ArcMenu menu) {
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//                LayoutParams.WRAP_CONTENT,
//                LayoutParams.WRAP_CONTENT
//        );
//        params.setMargins(0, 0, (int)right, (int)bottom);
//        menu.setLayoutParams(params);

        final int itemCount = item_drawables.length;
        for (int i = 0; i < itemCount; i++) {
            ImageView item = new ImageView(context);
            item.setImageResource(item_drawables[i]);

            final int position = i;
            menu.addItem(item, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "position " + position, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    private void setupListView(View view) {
        // Setup pull to refresh
        pullToRefreshLayout = (PullToRefreshLayout) view.findViewById(R.id.pulltorefresh_live);
        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create().refreshOnUp(true).build())
                .allChildrenArePullable()
                .listener(this)
                .setup(pullToRefreshLayout);

        // Populate feed
        adapter = new InboxViewAdapter(getActivity(), 0, null);
        adapter.setThumbnailScreenWidth(getScreenWidth());
        listView.setAdapter(adapter);

        receiver = new SessionListReceiver(new Handler());
        getRaps(receiver);

        // Infinite listview loading!
        listView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if(!flagLoading) {
                    flagLoading = true;
                    paginateNextSessions(receiver);
                }
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        getRaps(receiver);
    }

    public void getRaps(ResultReceiver receiver) {
        Intent api = new Intent(getActivity(), APIService.class);
        api.putExtra(T.API_TYPE, T.GET_COMPLETE_SESSIONS);
        api.putExtra(T.RECEIVER, receiver);

        getActivity().startService(api);
    }

    public void paginateNextSessions(ResultReceiver receiver) {
        Log.d(TAG, "Paginating next session");
        if(paginator == null) {
            return;
        }

        if(paginator.getNext() == null) {
            // Only load sessions if we can
            Log.d(TAG, "No data");
            Toast.makeText(getActivity(), "Loaded all raps", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        String nextUrl = paginator.getNext().toString();

        Intent api = new Intent(getActivity(), APIService.class);
        api.putExtra(T.API_TYPE, T.PAGINATE_NEXT_SESSION);
        api.putExtra(T.RECEIVER, receiver);
        api.putExtra(T.NEXT_SESSION_URL, nextUrl);

        getActivity().startService(api);
    }

    public double getScreenWidth() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public InboxViewAdapter getAdapter () {
        return adapter;
    }

    private class SessionListReceiver extends ResultReceiver {
        public SessionListReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == 1) {
                Log.d(TAG, "APIService returned successfully with completed sessions");
                // hide progress bar
                progressBar.setVisibility(View.GONE);

                String result = resultData.getString("result");
                paginator = (new Gson()).fromJson(result, Paginator.class);

                sessions = paginator.getResults();
                sessionsList.addAll(Arrays.asList(sessions));
                adapter.setSessionsList(sessionsList);
                adapter.notifyDataSetChanged();

                flagLoading = false;
                pullToRefreshLayout.setRefreshComplete();
            } else {
                Log.d(TAG, "APIService get session failed?");
            }
        }
    }
}
