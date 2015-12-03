package com.github.kb36.tumblrdash.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.github.kb36.tumblrdash.R;
import com.github.kb36.tumblrdash.utils.Constants;
import com.tumblr.jumblr.exceptions.JumblrException;
import com.tumblr.jumblr.request.RequestBuilder;

import org.scribe.model.Token;

/**
 * Async task for logging in the user
 */
public class AsyncLoginTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "AsyncLoginTask";
    private Context mContext;
    private String email;
    private String password;
    private ILoginResult iLoginCallBack;
    private ProgressDialog mPd;

    /**
     * Interface for call back login result
     */
    public interface ILoginResult {
        public void onLoginResultAvailable(Boolean result);
    }

    public AsyncLoginTask(Context context,
                          String _email,
                          String _password,
                          ILoginResult _loginCallBack) {
        mContext = context;
        email = _email;
        password = _password;
        iLoginCallBack = _loginCallBack;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        RequestBuilder rb = new RequestBuilder(null);
        rb.setConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        try {
            Token token = rb.postXAuth(email, password);
            saveCreds(token);
            return true;
        } catch(JumblrException e) {
            Log.e(TAG, e.getErrors() + " " + e.getResponseCode());
        }
        return false;
    }

    @Override
    protected void onCancelled(Boolean result) {
        super.onCancelled(result);
        if(mPd.isShowing()) {
            mPd.dismiss();
        }
        iLoginCallBack.onLoginResultAvailable(result);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPd = new ProgressDialog(mContext);
        mPd.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(mPd.isShowing()) {
            mPd.dismiss();
        }
        iLoginCallBack.onLoginResultAvailable(result);
    }

    /**
     * method to save token credentials
     * @param token
     */
    private void saveCreds(Token token) {
        if(token != null) {
            SharedPreferences sp = mContext.getSharedPreferences(mContext.getString(R.string.pref_file_key),
                    mContext.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            //Log.d(TAG, "token: "+ token.getToken()+ " secret: "+ token.getSecret());
            editor.putString(mContext.getString(R.string.token), token.getToken());
            editor.putString(mContext.getString(R.string.secret), token.getSecret());
            editor.apply();
        }
    }
}
