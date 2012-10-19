package ru.android.hellslade.autochmo;

import java.util.List;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends ArrayAdapter<Bitmap> {
	private int mGalleryItemBackground;
//    private AutochmoApplication mAutochmo;

    public ImageAdapter(Activity activity, List<Bitmap> images) {
    	super(activity, 0, images);
//    	mAutochmo = (AutochmoApplication)activity.getApplication();
        TypedArray attr = activity.obtainStyledAttributes(R.styleable.GalleryTheme);
        mGalleryItemBackground = attr.getResourceId(R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        attr.recycle();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	Activity activity = (Activity) getContext();
        ImageView imageView = new ImageView(activity);

        Bitmap image = getItem(position);
        imageView.setImageBitmap(image);
        imageView.setLayoutParams(new Gallery.LayoutParams(250, 187));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}