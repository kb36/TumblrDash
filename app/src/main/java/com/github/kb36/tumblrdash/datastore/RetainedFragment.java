package com.github.kb36.tumblrdash.datastore;

import android.app.Fragment;
import android.os.Bundle;

import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * Mainly used for saving data across configuration changes.
 * This fragment doesn't have an UI.
 */
public class RetainedFragment extends Fragment {
    private boolean mIsLoadingComplete;
    private int nextPage;
    private List<Post> mResults;
    private String query;
    public RetainedFragment() {
        if(mResults == null) {
            mResults = new ArrayList<Post>();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<Post> getData() {
        return mResults;
    }

    public Post getDataItem(int pos) {
        return mResults.get(pos);
    }

    public void addData(List<Post> data) {
        mResults.addAll(data);
    }

    public void addDataItem(Post dataItem) {
        mResults.add(dataItem);
    }

    public void resetData() {
        mResults.clear();
    }

    public void resetLoadComplete() {
        mIsLoadingComplete = false;
    }

    public void setLoadComplete() {
        mIsLoadingComplete = true;
    }

    public boolean isLoadComplete() {
        return mIsLoadingComplete;
    }
}

