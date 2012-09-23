package ru.android.hellslade.autochmo;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;

import ru.android.hellslade.autochmo.AsyncImageLoader.ImageCallback;
import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryAdapter extends BaseAdapter{
    int GalItemBg;
    private AsyncImageLoader asyncImageLoader;
    private Activity activity;
    private HashMap<String, SoftReference<Drawable>> images;
    private List<String> image_keys;
    private Gallery gallery;
    
    public GalleryAdapter(Activity activity, List<String> image_keys, HashMap<String, SoftReference<Drawable>> images, Gallery gallery) {
    	this.activity = activity;
    	this.gallery = gallery;
        TypedArray typArray = activity.getBaseContext().obtainStyledAttributes(R.styleable.GalleryTheme);
        GalItemBg = typArray.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        typArray.recycle();
        this.image_keys = image_keys;
        this.images = images;
        asyncImageLoader = new AsyncImageLoader(activity);
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
    	ImageView imgView = (ImageView) gallery.findViewWithTag(imageUrl);
    	if (imgView == null)
    	{
    		imgView = new ImageView(activity);
    	}
    	imgView.setTag(imageUrl);

    	Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl, new ImageCallback() {
            public void imageLoaded(Drawable imageDrawable, String imageUrl) {
            	images.put(imageUrl, new SoftReference<Drawable>(imageDrawable));
            	ImageView imageViewByTag = (ImageView) gallery.findViewWithTag(imageUrl);
            	imageViewByTag.setLayoutParams(new Gallery.LayoutParams(160, 120));
            	//imageViewByTag.setScaleType(ImageView.ScaleType.FIT_XY);
            	imageViewByTag.setBackgroundResource(GalItemBg);
            	imageViewByTag.setImageDrawable(imageDrawable);
            }
        });
    	images.put(imageUrl, new SoftReference<Drawable>(cachedImage));
		imgView.setLayoutParams(new Gallery.LayoutParams(160, 120));
		//imgView.setScaleType(ImageView.ScaleType.FIT_XY);
		imgView.setBackgroundResource(GalItemBg);
		imgView.setImageDrawable(cachedImage);
    	return imgView;
    }
}
