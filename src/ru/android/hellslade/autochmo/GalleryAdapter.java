package ru.android.hellslade.autochmo;

import java.net.URLEncoder;
import java.util.List;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter{
    int GalItemBg;
    private Activity activity;
    private AutochmoApplication mAutochmo;
    private List<String> image_keys;
    private Gallery gallery;
    
    public GalleryAdapter(Activity activity, List<String> image_keys, Gallery gallery) {
    	this.activity = activity;
    	this.gallery = gallery;
        TypedArray typArray = activity.getBaseContext().obtainStyledAttributes(R.styleable.GalleryTheme);
        GalItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        typArray.recycle();
        this.image_keys = image_keys;
        mAutochmo = (AutochmoApplication)activity.getApplication();
    }
    public int getCount() {
    	return image_keys.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
    	String imageUrl = image_keys.get(position);
    	ImageView imgView = (ImageView) gallery.findViewWithTag(position);
    	if (imgView == null)
    	{
    		imgView = new ImageView(activity);
    		imgView.setLayoutParams(new Gallery.LayoutParams(250, 187));
    		imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    		imgView.setBackgroundResource(GalItemBg);
    	}
    	imgView.setTag(position);
    	mAutochmo.imageLoader.displayImage(imageUrl, imgView);
    	return imgView;
    }
}
