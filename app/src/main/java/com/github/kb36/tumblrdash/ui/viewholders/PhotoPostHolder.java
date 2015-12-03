package com.github.kb36.tumblrdash.ui.viewholders;

import android.widget.ImageView;
import android.widget.TextView;

import com.github.kb36.tumblrdash.ui.SquaredImageView;
import com.github.kb36.tumblrdash.utils.Constants;

/**
 * View Holder for photo post
 */
public class PhotoPostHolder {
    public ImageView avatarView;
    public TextView titleView;
    public TextView[] textView = new TextView[Constants.IMAGE_SET_MAX_SIZE];
    public SquaredImageView[] imageView = new SquaredImageView[Constants.IMAGE_SET_MAX_SIZE];
}
