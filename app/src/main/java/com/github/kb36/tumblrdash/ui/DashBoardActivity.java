package com.github.kb36.tumblrdash.ui;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.kb36.tumblrdash.R;
import com.github.kb36.tumblrdash.datastore.RetainedFragment;
import com.github.kb36.tumblrdash.tasks.AsyncDashboardFetcher;
import com.github.kb36.tumblrdash.tasks.AsyncLoginTask;
import com.github.kb36.tumblrdash.ui.adapter.CustomAdapter;
import com.github.kb36.tumblrdash.utils.Constants;
import com.squareup.picasso.Picasso;
import com.tumblr.jumblr.types.Post;

import java.util.List;

/**
 * Main activity for handling dashboard
 */
public class DashBoardActivity extends AppCompatActivity
        implements AsyncLoginTask.ILoginResult,
        AsyncDashboardFetcher.IResultsAvailable {

    private static final String TAG = "DashBoardActivity";
    private static final String RETAINED_FRAGMENT_TAG = "tumblrdash";

    private Context mContext;
    //for login
    private AsyncLoginTask mAsyncLoginTask;
    //for dahsboard data fetch
    private AsyncDashboardFetcher mAsyncDashBoardFetcher;

    private ListView mListView;
    private RetainedFragment mDataFragment;
    private CustomAdapter mAdapter;

    private ProgressBar mFooterProgressBar;
    private ProgressBar mMainProgressBar;
    private View mFooterView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if(isAlreadyAuthenticated()) {
            initializeDashBoardView();
        } else {
            initializeAuthenticationView();
        }
    }

    /**
     * initialize the dash board view
     */
    private void initializeDashBoardView() {
        setContentView(R.layout.activity_dash_board);
        initSystem();
    }

    /**
     * initializes the system
     */
    private void initSystem() {
        //Mainly used for data store across config changes
        FragmentManager fm = getFragmentManager();
        mDataFragment = (RetainedFragment) fm.findFragmentByTag(RETAINED_FRAGMENT_TAG);
        if(mDataFragment == null) {
            mDataFragment = new RetainedFragment();
            fm.beginTransaction().add(mDataFragment, RETAINED_FRAGMENT_TAG).commit();
        }

        mListView = (ListView) findViewById(R.id.listView);
        mMainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);

        mAdapter = new CustomAdapter(this, R.layout.text_post_layout, mDataFragment.getData());

        mListView.setEmptyView(mMainProgressBar);
        mListView.setAdapter(mAdapter);

        mFooterView = LayoutInflater.from(this).inflate(R.layout.progress_bar_layout, null);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.progressBar);
        mFooterProgressBar.setVisibility(View.GONE);
        mListView.addFooterView(mFooterView);

        //add scroll listener for paging the results
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    Picasso.with(mContext).resumeTag(mContext);
                } else {
                    Picasso.with(mContext).pauseTag(mContext);
                }
                if (scrollState == SCROLL_STATE_IDLE) {
                    Log.d(TAG, "last visible: " + mListView.getLastVisiblePosition());
                    if (mListView.getLastVisiblePosition() >= mDataFragment.getData().size() - 1) {
                        if (canFetchMore()) {
                            if (!isFooterProgressVisible()) {
                                Log.d(TAG, "making footer visible");
                                mFooterProgressBar.setVisibility(View.VISIBLE);
                            }
                            startDashboardFetchTask(mDataFragment.getData().size());
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //user wants to refresh the data. Reset load complete
                mDataFragment.resetLoadComplete();
                startDashboardFetchTask(0);
            }
        });

        mAdapter.notifyDataSetChanged();

        //first fetch
        if(mDataFragment.getData().size() == 0 &&
                canFetchMore()) {
            setMainProgressBarVisibility(View.VISIBLE);
            startDashboardFetchTask(mDataFragment.getData().size());
        }

    }

    /**
     * change activiy layout for user authentication
     */
    private void initializeAuthenticationView() {
        //load login screen
        setContentView(R.layout.activity_login_screen);
        Button login = (Button) findViewById(R.id.login);
        final EditText email = (EditText) findViewById(R.id.email);
        final EditText password = (EditText) findViewById(R.id.passsword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().isEmpty())
                    Toast.makeText(mContext, "Please enter valid email", Toast.LENGTH_SHORT).show();
                else if (password.getText().toString().isEmpty())
                    Toast.makeText(mContext, "Please enter valid password", Toast.LENGTH_SHORT).show();
                else {
                    startLoginTask(email.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    /**
     * start fetching the dash board results from given offset
     * @param offset
     */
    private void startDashboardFetchTask(Integer offset) {
        //read token and shared secret
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        String token = sp.getString(getString(R.string.token), null);
        String secret = sp.getString(getString(R.string.secret), null);

        //Log.d(TAG, "token: "+ token+ " secret: "+ secret);
        mAsyncDashBoardFetcher = new AsyncDashboardFetcher(token, secret, this);
        mAsyncDashBoardFetcher.execute(offset);
    }

    /**
     * starts task for aysnchronoulsy authenticating the user
     * @param email
     * @param pass
     */
    private void startLoginTask(String email, String pass) {
        mAsyncLoginTask  = new AsyncLoginTask((Context)this, email, pass, this);
        mAsyncLoginTask.execute();
    }

    /**
     * call back indicating the login result
     * @param result
     */
    @Override
    public void onLoginResultAvailable(Boolean result) {
        if(result == null || !result) {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        } else {
            initializeDashBoardView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAsyncLoginTask != null &&
                mAsyncLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAsyncLoginTask.cancel(true);
        }
        mAsyncLoginTask = null;
        if(mAsyncDashBoardFetcher != null &&
                mAsyncDashBoardFetcher.getStatus() == AsyncTask.Status.RUNNING) {
            mAsyncDashBoardFetcher.cancel(true);
        }
        mAsyncDashBoardFetcher = null;
    }

    /**
     * checks whether application is already authenticated
     * @return
     */
    private boolean isAlreadyAuthenticated()  {
        SharedPreferences sp = getSharedPreferences(getString(R.string.pref_file_key),
                Context.MODE_PRIVATE);
        String token = sp.getString(getString(R.string.token), null);
        String secret = sp.getString(getString(R.string.secret), null);
        if(token != null && secret != null)
            return true;
        else
            return false;
    }

    /**
     * checks whether more data can be fetched.
     * @return
     */
    private boolean canFetchMore() {
        boolean isRunning = (mAsyncDashBoardFetcher == null ? false :
                (mAsyncDashBoardFetcher.getStatus() == AsyncDashboardFetcher.Status.RUNNING));
        Log.d(TAG, "mIsLoadComplete: " + mDataFragment.isLoadComplete()
                + " " + " is_running: " + isRunning);
                ;
        return !mDataFragment.isLoadComplete() && !isRunning;
    }

    /**
     * handlre received data results
     * @param results
     */
    @Override
    public void onResultsAvailable(List<Post> results) {
        //Refresh result received
        if(mSwipeRefreshLayout != null && mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            if(results != null && results.size() > 0) {
                Log.d(TAG, "refresh data received: ");
                mDataFragment.resetData();
                mDataFragment.addData(results);
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
        if(isFooterProgressVisible()) {
            mFooterProgressBar.setVisibility(View.GONE);
        }
        setMainProgressBarVisibility(View.GONE);

        if(results == null || results.size() == 0) {
            if(mDataFragment.getData().size() == 0)
                Toast.makeText(mContext, "No results", Toast.LENGTH_SHORT).show();
            mDataFragment.setLoadComplete();
        } else {
            mDataFragment.addData(results);
            if(results.size() < Constants.RESULT_LIMIT)
                mDataFragment.setLoadComplete();
            mAdapter.notifyDataSetChanged();
        }
        Log.d(TAG, "data size: " + mDataFragment.getData().size());
    }

    /**
     * returns whether bottom progress bar is visible
     * @return
     */
    private boolean isFooterProgressVisible() {
        return mFooterProgressBar != null &&
                (mFooterProgressBar.getVisibility() == View.VISIBLE);
    }

    /**
     * change visibility of default progressbar
     * @param visible
     */
    private void setMainProgressBarVisibility(int visible) {
        if(mMainProgressBar != null)
            mMainProgressBar.setVisibility(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                resetCreds();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * resets login data for log out
     */
    private void resetCreds() {
        SharedPreferences sp = mContext.getSharedPreferences(mContext.getString(R.string.pref_file_key),
                mContext.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        //Log.d(TAG, "token: "+ token.getToken()+ " secret: "+ token.getSecret());
        editor.putString(mContext.getString(R.string.token), null);
        editor.putString(mContext.getString(R.string.secret), null);
        editor.apply();
    }
}
