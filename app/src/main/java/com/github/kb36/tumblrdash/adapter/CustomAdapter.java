package com.github.kb36.tumblrdash.adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.kb36.tumblrdash.R;
import com.github.kb36.tumblrdash.adapter.holders.PhotoPostHolder;
import com.github.kb36.tumblrdash.utils.Constants;
import com.tumblr.jumblr.types.Photo;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.PhotoSize;
import com.tumblr.jumblr.types.Post;
import com.tumblr.jumblr.types.TextPost;

import java.util.List;

/**
 * Custom adapter which backs the dashboard
 */
public class CustomAdapter extends ArrayAdapter<Post> {
    private final static String TAG = "CustomAdapter";
    private Context mContext;
    private int mResource;
    private LayoutInflater mInflater;

    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;


    /**
     * constructor for custom adapter
     * @param context
     * @param resource
     */
    public CustomAdapter(Context context, int resource, List<Post> data) {
        super(context, resource, data);
        mContext = context;
        mResource = resource;
        mInflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(getItem(position).getType().equals(Constants.TYPE_PHOTO)) {
            Log.d(TAG, "returning photo post");
            return VIEW_TYPE_PHOTO;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    /**
     * Method for loading item layouts in list view
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(getItemViewType(position) == VIEW_TYPE_PHOTO) {
            return updatePhotoData((PhotoPost)getItem(position), convertView, parent);
        } else {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(mResource, null);
                holder = new ViewHolder();
                holder.avatarView = (ImageView) convertView.findViewById(R.id.avatarView);
                holder.titleView = (TextView) convertView.findViewById(R.id.titleView);
                holder.headerView = (TextView) convertView.findViewById(R.id.headerView);
                holder.bodyView = (TextView) convertView.findViewById(R.id.bodyView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            //Log.d(TAG, "Getting the view at position:" + position);

            Post post = getItem(position);

            String blogName = post.getBlogName();
            //String url = Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX;
            //Log.d(TAG, "blog name: "+ blogName+ "url: "+ url);

            if (blogName != null) {
                Glide.with(mContext)
                        .load(Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .crossFade()
                        .into(holder.avatarView);
                holder.titleView.setText(post.getBlogName());
            }

            Log.d(TAG, post.getType());
            //TEXT
            if (post.getType().equals(Constants.TYPE_TEXT)) {
                TextPost textPost = (TextPost) post;
                if (textPost.getTitle() != null) {
                    holder.headerView.setText("");
                    holder.headerView.setText(Html.fromHtml(textPost.getTitle()));
                }
                if (textPost.getBody() != null) {
                    holder.bodyView.setText("");
                    holder.bodyView.setText(Html.fromHtml(textPost.getBody()));
                }
            } else if (post.getType().equals(Constants.TYPE_PHOTO)) {

            }
            return convertView;
        }
    }

    /**
     * View Holder to hold the item layout for
     * performance
     */
    private class ViewHolder {
        ImageView avatarView;
        TextView titleView, headerView, bodyView;
    }

    /**
     * handle loading photo data
     * @return
     */
    private View updatePhotoData(PhotoPost post, View convertView, ViewGroup parent) {
        PhotoPostHolder holder = null;
        convertView = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.image_post_layout, null);
            holder = new PhotoPostHolder();

            holder.avatarView = (ImageView) convertView.findViewById(R.id.avatarView);
            holder.titleView = (TextView) convertView.findViewById(R.id.titleView);

            holder.imageView[0] = (ImageView) convertView.findViewById(R.id.imageView00);
            holder.imageView[1] = (ImageView) convertView.findViewById(R.id.imageView01);
            holder.imageView[2] = (ImageView) convertView.findViewById(R.id.imageView02);
            holder.imageView[3] = (ImageView) convertView.findViewById(R.id.imageView03);
            holder.imageView[4] = (ImageView) convertView.findViewById(R.id.imageView04);
            holder.imageView[5] = (ImageView) convertView.findViewById(R.id.imageView05);
            holder.imageView[6] = (ImageView) convertView.findViewById(R.id.imageView06);
            holder.imageView[7] = (ImageView) convertView.findViewById(R.id.imageView07);
            holder.imageView[8] = (ImageView) convertView.findViewById(R.id.imageView08);
            holder.imageView[9] = (ImageView) convertView.findViewById(R.id.imageView09);

            holder.textView[0] = (TextView) convertView.findViewById(R.id.textView00);
            holder.textView[1] = (TextView) convertView.findViewById(R.id.textView01);
            holder.textView[2] = (TextView) convertView.findViewById(R.id.textView02);
            holder.textView[3] = (TextView) convertView.findViewById(R.id.textView03);
            holder.textView[4] = (TextView) convertView.findViewById(R.id.textView04);
            holder.textView[5] = (TextView) convertView.findViewById(R.id.textView05);
            holder.textView[6] = (TextView) convertView.findViewById(R.id.textView06);
            holder.textView[7] = (TextView) convertView.findViewById(R.id.textView07);
            holder.textView[8] = (TextView) convertView.findViewById(R.id.textView08);
            holder.textView[9] = (TextView) convertView.findViewById(R.id.textView09);

            convertView.setTag(holder);
        }
        holder = (PhotoPostHolder) convertView.getTag();
        for(int i = 0; i < 10; i++) {
            holder.textView[i].setVisibility(View.GONE);
            holder.imageView[i].setImageDrawable(null);
            holder.imageView[i].setVisibility(View.GONE);
        }
        //Log.d(TAG, "Getting the view at position:" + position);

        String blogName = post.getBlogName();
        //String url = Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX;
        //Log.d(TAG, "blog name: "+ blogName+ "url: "+ url);

        if(blogName != null) {
            Glide.with(mContext)
                    .load(Constants.BLOG_URL_PREFIX + blogName + Constants.BLOG_URL_SUFFIX)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .crossFade()
                    .into(holder.avatarView);
            holder.titleView.setText(post.getBlogName());
        }

        int i = 0;
        for(Photo p : post.getPhotos()) {
            Log.d(TAG, "cap: "+ p.getCaption()+ " "+ p.getOriginalSize().getUrl() );
            if(p.getCaption() != null) {
                holder.textView[i].setText(p.getCaption());
                holder.textView[i].setVisibility(View.VISIBLE);
            }
            if(p.getSizes() != null && p.getSizes().size() > 0) {
                int minWidth = Integer.MAX_VALUE;
                String url = null;
                for(PhotoSize ps: p.getSizes()) {
                    if(ps.getWidth() < minWidth) {
                        minWidth = ps.getWidth();
                        url = ps.getUrl();
                    }
                }
                Log.d(TAG, "minWidth: "+ minWidth);
                holder.imageView[i].setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .crossFade()
                        .into(holder.imageView[i]);
            }
            i++;
        }
        return convertView;
    }
}
