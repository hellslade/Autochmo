package ru.android.hellslade.autochmo;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    public List<Bitmap> mImages;

    public ImageAdapter(Context c, List<Bitmap> images) {
        mImages = images;
        mContext = c;
        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.GalleryTheme);
        mGalleryItemBackground = attr.getResourceId(
                R.styleable.GalleryTheme_android_galleryItemBackground, 0);
        attr.recycle();
    }

    public int getCount() {
        return mImages.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);

        imageView.setImageBitmap(mImages.get(position));
        imageView.setLayoutParams(new Gallery.LayoutParams(250, 187));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}