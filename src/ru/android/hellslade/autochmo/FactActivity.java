package ru.android.hellslade.autochmo;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
	        return api._getComment(fact.getFactId());
		}
		@Override
		protected void onPostExecute(List<Comment> result) {
			comments = result;
	        commentListView.setAdapter(null);
	        CommentListAdapter listAdapter = new CommentListAdapter(FactActivity.this, comments, commentListView);
	        commentListView.setAdapter(listAdapter);
	        commentListView.invalidateViews();
			pg.dismiss();
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
			return api.setRate(params[0], params[1]);
		}
		@Override
		protected void onPostExecute(String result) {
			pg.dismiss();
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
			return api._addComment(params[0], params[1]);
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
	public static final String COMMENT = "comment";
	public static final String COUNT = "count";
	private Fact fact;
	private Comment comment;
	private List<Comment> comments;
	public ListView commentListView;
	private Gallery gallery;
    private ImageView imgView;
    private HashMap<String, SoftReference<Drawable>> images;
    private List<String> image_keys;
    private AutochmoAPI api;
    private static final int REQUEST_ADD_COMMENT = 0x000001;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fact_activity);
		images = new HashMap<String, SoftReference<Drawable>>();
		image_keys = new ArrayList<String>();
		comments = new ArrayList<Comment>();
		commentListView = (ListView) findViewById(R.id.commentListView);
		api = new AutochmoAPI(this);
		
		Bundle extras = getIntent().getExtras();
		fact = extras.getParcelable(FACT);
		int count = extras.getInt(COUNT);
//		for (int i = count-1; i > -1; i--)
		for (int i = 0; i < count ; i++)
		{
			comment = extras.getParcelable(COMMENT+i);
			comments.add(comment);
		}
		ListView mListView = (ListView) findViewById(R.id.commentListView);
		LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.fact_activity_header, null);
		mListView.addHeaderView(v);
		
		ExpandablePanel panel = (ExpandablePanel) findViewById(R.id.commentTextViewFoo);
		panel.setOnExpandListener(listener);
		
		TextView rateMinus = (TextView) findViewById(R.id.rateMinusTextView);
		rateMinus.setOnClickListener(this);
		
		TextView ratePlus = (TextView) findViewById(R.id.ratePlusTextView);
        ratePlus.setOnClickListener(this);
        
        imgView = (ImageView)findViewById(R.id.factImageView);
        //ImageView imageView = (ImageView)findViewById(R.id.factImageView);
        imgView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                String fileSource = (String) imgView.getTag();
                Log.v("fileSource" + fileSource);
                String filePath = getExternalCacheDir() + "/.image-cache/" + fileSource.hashCode() + ".jpg";
                //intent.setDataAndType(Uri.parse(fileSource), "image/*");
                intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
                startActivity(intent);
            }
        });
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
	        	//new RefreshTask().execute();
	        }
	     }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	public void reloadComments_old() {
	    // перезагрузить комментарии
        ListView commentListView = (ListView) findViewById(R.id.commentListView);
        comments = api._getComment(fact.getFactId());
        commentListView.setAdapter(null);
        CommentListAdapter listAdapter = new CommentListAdapter(FactActivity.this, comments, commentListView);
        commentListView.setAdapter(listAdapter);
        commentListView.invalidateViews();
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
	ExpandablePanel.OnExpandListener listener = new ExpandablePanel.OnExpandListener() {
        @Override
        public void onExpand(View handle, View content) {
            Button btn = (Button) handle;
            btn.setText(getResources().getString(R.string.show_less));
        }
        @Override
        public void onCollapse(View handle, View content) {
            Button btn = (Button) handle;
            btn.setText(getResources().getString(R.string.show_more));
        }
    };
	public void fillData() {
		image_keys.clear();
		images.clear();
		for (int i = 0; i < fact.getPictureCount(); i++)
		{
			Log.v("i = " + i);
			image_keys.add(getString(R.string.host_image)+fact.getPictureMedium(i).trim());
		}
//		http://www.androidpeople.com/android-gallery-imageview-example
        gallery = (Gallery) findViewById(R.id.pictureGallery);
        GalleryAdapter adt = new GalleryAdapter(this, image_keys, images, gallery);
		gallery.setAdapter(adt);
		gallery.setOnItemClickListener(onItemClickListener);
		
		ListView commentListView = (ListView) findViewById(R.id.commentListView);
		CommentListAdapter listAdapter = new CommentListAdapter(FactActivity.this, comments, commentListView);
		commentListView.setAdapter(listAdapter);
		
        TextView commentTextView = (TextView)findViewById(R.id.commentTextView);
		commentTextView.setText(fact.getComment());
		/*
		TextView numberTextView = (TextView)findViewById(R.id.numberTextView);
		numberTextView.setText(fact.getGosnomer().toUpperCase());
		*/
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
		dateTextView.setText(" " + fact.getDatecreatedStr());
		
		
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
	OnItemClickListener onItemClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView parent, View v, int position, long id) {
/*			Log.v("Position " + position);
			Log.v("images.size() " + images.size());
*/			
			String imageUrl = (String)v.getTag();
			imgView.setImageDrawable((Drawable)images.get(imageUrl).get());
			imgView.setTag(imageUrl);
        }
	};
}
