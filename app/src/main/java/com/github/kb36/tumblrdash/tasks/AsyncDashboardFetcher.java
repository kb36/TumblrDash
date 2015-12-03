package com.github.kb36.tumblrdash.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.github.kb36.tumblrdash.utils.Constants;
import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * task class for fetching tumblr dashboard feed
 */
public class AsyncDashboardFetcher  extends AsyncTask<Integer, Void, List<Post>> {
    private static final String TAG = "AsyncDashboardFetcher";

    private String token;
    private String secret;
    private JumblrClient mClient;
    private IResultsAvailable resultsCallBack;

    public interface IResultsAvailable {
        public void onResultsAvailable(List<Post> results);
    }

    public AsyncDashboardFetcher(String _token, String _secret, IResultsAvailable callback) {
        token = _token;
        secret = _secret;
        mClient = new JumblrClient(Constants.CONSUMER_KEY,
                Constants.CONSUMER_SECRET);
        //Log.d(TAG, "token: "+ token + "secret: "+ secret);
        mClient.setToken(token, secret);
        resultsCallBack = callback;
    }

    @Override
    protected List<Post> doInBackground(Integer... offset) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("limit", Constants.RESULT_LIMIT);
        params.put("offset", offset[0]);
        return mClient.userDashboard(params);
    }

    @Override
    protected void onPostExecute(List<Post> posts) {
        super.onPostExecute(posts);
        resultsCallBack.onResultsAvailable(posts);
    }

    @Override
    protected void onCancelled(List<Post> posts) {
        super.onCancelled(posts);
        resultsCallBack.onResultsAvailable(posts);
    }
}
