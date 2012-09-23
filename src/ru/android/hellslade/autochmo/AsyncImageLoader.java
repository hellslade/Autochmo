package ru.android.hellslade.autochmo;
//see http://blog.dutchworks.nl/2009/09/17/exploring-the-world-of-android-part-2/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {
    //private HashMap<String, SoftReference<Drawable>> imageCache;
	private Context context;
	final public String EMPTY_IMAGE = "empty_image"; 
 
    public AsyncImageLoader(Context c) {
    	//imageCache = new HashMap<String, SoftReference<Drawable>>();
    	this.context = c;
    }
    public Drawable loadFromLocalCache(String mLocal)
    {
    	Drawable d = Drawable.createFromPath(mLocal);
    	return d;
    }
    public Drawable loadDrawable(final String imageUrl, final ImageCallback imageCallback) {
    	if (imageUrl.equalsIgnoreCase(EMPTY_IMAGE))
    	{
    		return context.getResources().getDrawable(R.drawable.no_image);
    	}
    	
    	String mLocal = context.getExternalCacheDir() + "/.image-cache/" + imageUrl.hashCode() + ".jpg";
    	Log.v("Local cache path " + mLocal);
    	final File local = new File(mLocal);
    	if (local.exists()) {
		    Drawable drawable = loadFromLocalCache(mLocal);
		    if (drawable != null) {
		    	Log.v("Image loaded from local cache");
		        return drawable;
		    }
        } else {
            local.getParentFile().mkdirs();
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
            }
        };
        final Thread thread = new Thread() {
            @Override
            public void run() {
                Drawable drawable = loadImageFromUrl(imageUrl);
				//imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
				try {
					local.createNewFile();
				} catch (IOException e1) {
					Log.v("Error to create file " + local.getAbsolutePath());
				}
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(local);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				} catch (FileNotFoundException e) {
					Log.v("Error to cache image " + e.getMessage());
				}
				Message message = handler.obtainMessage(0, drawable);
                handler.sendMessage(message);
            }
        };//.start();
        thread.setPriority(3);
        thread.start();
        return null;
    }
 
    public static Drawable loadImageFromUrl(String url) {
    	InputStream inputStream;
        try {
        	Log.v(url);
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Drawable.createFromStream(inputStream, "src");
    }
 
    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, String imageUrl);
    }
}