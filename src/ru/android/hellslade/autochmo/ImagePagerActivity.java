package ru.android.hellslade.autochmo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author 
 */
public class ImagePagerActivity extends Activity{
	public static final String IMAGES = "universalimageloader.IMAGES";
	public static final String IMAGE_POSITION = "universalimageloader.IMAGE_POSITION";
	
	private AutochmoApplication mAutochmo;
	private CustomViewPager pager;
	private TextView tv;
	private int mCountAll;

	//private DisplayImageOptions options;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mAutochmo = (AutochmoApplication) getApplication();
		
		setContentView(R.layout.image_pager_layout);
		Bundle bundle = getIntent().getExtras();
		String[] imageUrls = bundle.getStringArray(IMAGES);
		mCountAll = imageUrls.length;
		int pagerPosition = bundle.getInt(IMAGE_POSITION, 0);

//		options = new DisplayImageOptions.Builder()
//			.showImageForEmptyUri(R.drawable.ic_launcher_autochmo)
//			.cacheOnDisc()
//			.imageScaleType(ImageScaleType.EXACT)
//			.build();
		
		tv = (TextView)findViewById(R.id.numTextView);
		tv.setText(String.format("%s/%s", pagerPosition+1, mCountAll));
		
		pager = (CustomViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
		pager.setOnPageChangeListener(listener);
//		pager.requestDisallowInterceptTouchEvent(true);
	}
	
	OnPageChangeListener listener = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			tv.setText(String.format("%s/%s", position+1, mCountAll));
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
	};
	@Override
	protected void onStop() {
		mAutochmo.imageLoader.stop();
		super.onStop();
	}
	private class ImagePagerAdapter extends PagerAdapter {

		private String[] images;
		private LayoutInflater inflater;

		ImagePagerAdapter(String[] images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((CustomViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object instantiateItem(View view, int position) {
			final FrameLayout imageLayout = (FrameLayout) inflater.inflate(R.layout.image_pager_item_layout, null);
			final TouchImageView imageView = (TouchImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			
			imageView.setPagerView(pager);
			
			mAutochmo.imageLoader.displayImage(images[position], imageView, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted() {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(FailReason failReason) {
					String message = null;
					switch (failReason) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
					imageView.setImageResource(android.R.drawable.ic_delete);
				}

				@Override
				public void onLoadingComplete(Bitmap bmp) {
					spinner.setVisibility(View.GONE);
					Animation anim = AnimationUtils.loadAnimation(ImagePagerActivity.this, R.anim.fade_in);
					imageView.setAnimation(anim);
					anim.start();
				}

				@Override
				public void onLoadingCancelled() {
					// Do nothing
				}
			});

			((CustomViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
}