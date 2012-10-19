package ru.android.hellslade.autochmo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FactActivity extends SherlockFragmentActivity implements OnClickListener {
	private class RefreshTask extends AsyncTask<Void, Void, List<Comment>> {
		ProgressDialog pg = new ProgressDialog(FactActivity.this);
		@Override
		protected void onPreExecute() {
			pg.setMessage(getApplicationContext().getResources().getString(R.string.refresh_task_message));
			pg.show();
			super.onPreExecute();
		}
		@Override
		protected List<Comment> doInBackground(Void... params) {
	        return mAutochmo._getComment(fact.getFactId());
		}
		@Override
		protected void onPostExecute(List<Comment> result) {
			pg.dismiss();
			if (mAdapter == null) {
				mAdapter = new CommentListAdapter(FactActivity.this, result, commentListView);
				commentListView.setAdapter(mAdapter);
			} else {
				mAdapter.clear();
				for (Comment c : result) {
					mAdapter.add(c);
				}
			}
//	        commentListView.invalidateViews();
			super.onPostExecute(result);
		}
	}
	private class VoteTask extends AsyncTask<String, Void, String> {
		ProgressDialog pg = new ProgressDialog(FactActivity.this);
		@Override
		protected void onPreExecute() {
			pg.setMessage(getApplicationContext().getResources().getString(R.string.vote_task_message));
			pg.show();
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			return mAutochmo.setRate(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result) {
			pg.dismiss();
			//  ак-то надо изменить рейтинг факта (и в ленте тоже обновить)
			Toast.makeText(FactActivity.this, result, Toast.LENGTH_LONG).show();
			super.onPostExecute(result);
		}
	}
	private class AddCommentTask extends AsyncTask<String, Void, String> {
		ProgressDialog pg = new ProgressDialog(FactActivity.this);
		@Override
		protected void onPreExecute() {
			pg.setMessage(getApplicationContext().getResources().getString(R.string.add_comment_task_message));
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... params) {
			return mAutochmo._addComment(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result) {
			pg.dismiss();
			Toast.makeText(FactActivity.this, result, Toast.LENGTH_LONG).show();
			new RefreshTask().execute();
			super.onPostExecute(result);
		}
	}
	
	public static final String FACT = "fact";
	private Fact fact;
	private CommentListAdapter mAdapter;
	public ListView commentListView;
	private Gallery gallery;
    private ImageView imgView;
    private List<String> image_keys;
    private AutochmoApplication mAutochmo;
    private static final int REQUEST_ADD_COMMENT = 0x000001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAutochmo = (AutochmoApplication)getApplication();
		setContentView(R.layout.fact_activity);
		image_keys = new ArrayList<String>();

		commentListView = (ListView) findViewById(R.id.commentListView);
		mAutochmo = (AutochmoApplication)getApplication();
		
		Bundle extras = getIntent().getExtras();
		fact = extras.getParcelable(FACT);

		LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.fact_activity_header, null);
        commentListView.addHeaderView(v);
		
		TextView rateMinus = (TextView) findViewById(R.id.rateMinusTextView);
		rateMinus.setOnClickListener(this);
		
		TextView ratePlus = (TextView) findViewById(R.id.ratePlusTextView);
        ratePlus.setOnClickListener(this);
        
        imgView = (ImageView)findViewById(R.id.factImageView);
        
        fillData();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_fact, menu);
        return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case (R.id.itemAddComment):
                // Launch Preference activity
                Intent intent = new Intent(FactActivity.this, CommentAddActivity.class);
                startActivityForResult(intent, REQUEST_ADD_COMMENT);
                break;
            case (R.id.itemRefreshComment):
            	new RefreshTask().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_ADD_COMMENT) {
	        if (resultCode == RESULT_OK) {
	        	new AddCommentTask().execute(fact.getFactId(), data.getStringExtra("comment"));
	        }
	     }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	public void onClick(View v) {
	    switch (v.getId()) {
	        case (R.id.rateMinusTextView):
	            new VoteTask().execute(fact.getFactId(), "-1");
	            break;
	        case (R.id.ratePlusTextView):
	        	new VoteTask().execute(fact.getFactId(), "1");
	            break;
	    }
	}
	public void fillData() {
		image_keys.clear();
		for (int i = 0; i < fact.getPictureCount(); i++)
		{
//			Log.v("i = " + i);
			image_keys.add(getString(R.string.host_image)+fact.getPictureMedium(i).trim());
		}
//		http://www.androidpeople.com/android-gallery-imageview-example
        gallery = (Gallery) findViewById(R.id.pictureGallery);
        GalleryAdapter adt = new GalleryAdapter(this, image_keys, gallery);
		gallery.setAdapter(adt);
		gallery.setOnItemClickListener(onItemClickListener);
		
		new RefreshTask().execute();
		
        TextView commentTextView = (TextView)findViewById(R.id.commentTextView);
		commentTextView.setText(fact.getComment());

		TextView nomerTextView = (TextView)findViewById(R.id.nomerView);
		nomerTextView.setText(fact.getGosnomerNomer().toUpperCase());
        
        TextView regionTextView = (TextView)findViewById(R.id.regionView);
        regionTextView.setText(fact.getGosnomerRegion().toUpperCase());
        
        int[] i = getNomerShape(fact.getGosnomerType());
        nomerTextView.setBackgroundResource(i[0]);
        regionTextView.setBackgroundResource(i[1]);
        /*  */
		TextView ratePlusTextView = (TextView)findViewById(R.id.ratePlusTextView);
		ratePlusTextView.setText(" " + fact.getRatingPlus());
		
		TextView rateMinusTextView = (TextView)findViewById(R.id.rateMinusTextView);
		rateMinusTextView.setText(" " + fact.getRatingMinus());
		
		TextView carmodelTextView = (TextView)findViewById(R.id.carmodelTextView);
		carmodelTextView.setText(String.format("%s %s", fact.getCarModelManufacturer(), fact.getCarModel()));
		
		TextView dateTextView = (TextView)findViewById(R.id.dateTextView);
		
		SimpleDateFormat sdf_parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			Date date = sdf_parse.parse(fact.getDatecreatedStr());
			dateTextView.setText(DateFormat.getLongDateFormat(getApplicationContext()).format(date) + " " + DateFormat.getTimeFormat(getApplicationContext()).format(date));
		} catch (ParseException e) {
			dateTextView.setText(" " + fact.getDatecreatedStr());
		}
		
		
	};
	public int[] getNomerShape(String typ) {
	    int[] result;// = new int[]{R.drawable.nomer_rus, R.drawable.region_rus};
	    if (typ.equalsIgnoreCase("RUS")) {
	        result = new int[]{R.drawable.nomer_rus, R.drawable.region_rus};
	    } else if (typ.equalsIgnoreCase("DIP")) {
	        result = new int[]{R.drawable.nomer_dip, R.drawable.region_dip};
	    } else if (typ.equalsIgnoreCase("MO")) {
	        result = new int[]{R.drawable.nomer_mo, R.drawable.region_mo};
        } else if (typ.equalsIgnoreCase("MVD")) {
            result = new int[]{R.drawable.nomer_mvd, R.drawable.region_mvd};
        } else if (typ.equalsIgnoreCase("TRANS")) {
            result = new int[]{R.drawable.nomer_trans, R.drawable.region_trans};
        } else if (typ.equalsIgnoreCase("PUB")) {
            result = new int[]{R.drawable.nomer_pub, R.drawable.region_pub};
        } else {
            Log.v("ELSE " + typ);
            result = new int[]{R.drawable.nomer_rus, R.drawable.region_rus};
        }
	    return result;
	}
	private void startImageGalleryActivity(int position) {
		String[] urls = new String[fact.getPictureCount()];
		for (int i=0; i<fact.getPictureCount(); i++) {
			urls[i] = this.getResources().getString(R.string.host_image) + fact.getPictureOriginal(i);
		}
		
		Intent intent = new Intent(this, ImagePagerActivity.class);
		intent.putExtra(ImagePagerActivity.IMAGES, urls);
		intent.putExtra(ImagePagerActivity.IMAGE_POSITION, position);
		startActivity(intent);
	}
	OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView parent, View v, int position, long id) {
			//Toast.makeText(mAutochmo, "ќткрыть Activity с ViewPager дл€ просмотра", Toast.LENGTH_LONG).show();
			startImageGalleryActivity(position);
        }
	};
}
