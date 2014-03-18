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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.encore.API.APIService;
import com.encore.InboxViewAdapter;
import com.encore.R;
import com.encore.SessionFlowManager;
import com.encore.models.Paginator;
import com.encore.models.Session;
import com.encore.util.T;
import com.encore.widget.ArcLayout;
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

public class LiveFragment extends Fragment implements OnRefreshListener {
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

    private boolean isMenuDrawn = false;
    private boolean isLayoutDrawn = false;
    private boolean isMarginSet = false;

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

        // Pop off the login screen, if it exists
        getFragmentManager().popBackStack();
    }

    private void getViews(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progress_live);
        listView = (ListView) view.findViewById(R.id.video_list_view2);
        arcMenu = (ArcMenu) view.findViewById(R.id.arc_menu);

        // Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);
    }

    public void setupArcMenu(int[] item_drawables, ArcMenu menu) {

        ImageView freestyleButton = new ImageView(context);
        freestyleButton.setImageResource(item_drawables[0]);

        menu.addItem(freestyleButton, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch freestyle
                Intent createFreestyle = new Intent(context, SessionFlowManager.class);
                startActivity(createFreestyle);
            }
        });

        ImageView battlesButton = new ImageView(context);
        battlesButton.setImageResource(item_drawables[1]);

        menu.addItem(battlesButton, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch battles (friend picker dialog or something)
                Intent createBattle = new Intent(context, FriendPickerActivity.class);
                createBattle.setFlags(createBattle.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(createBattle);
            }
        });

        // Draw in bottom right corner
        ArcLayout arcLayout = menu.getArcLayout();

        menu.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                isMenuDrawn = true;
                setArcMenuMargins();

                return true;
            }
        });

        arcLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                isLayoutDrawn = true;
                setArcMenuMargins();

                return true;
            }
        });
    }

    private void setArcMenuMargins() {
        if(isMenuDrawn && isLayoutDrawn && !isMarginSet) {
            ArcLayout arcLayout = arcMenu.getArcLayout();

            final int arcMenuHeight = arcMenu.getMeasuredHeight();
            final int arcMenuWidth = arcMenu.getMeasuredWidth();
            final int iconHeight = arcLayout.getMeasuredHeight();
            final int iconWidth = arcLayout.getMeasuredWidth();

            // TODO: Test if these margins work on other devices too
            float marginRight = (iconHeight - arcMenuWidth)/2,
                    marginBottom = (iconWidth - arcMenuWidth)/2;

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, (int)marginRight, (int)marginBottom);
            arcMenu.setLayoutParams(params);

            isMarginSet = true;
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
		api.putExtra(T.API_TYPE, T.GET_LIVE_SESSIONS);
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
                Log.d(TAG, "APIService returned successfully with live sessions");
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
