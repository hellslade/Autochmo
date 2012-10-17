package ru.android.hellslade.autochmo;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class LentaActivity extends SherlockFragmentActivity {
    class GetFactTask extends AsyncTask<Integer, Void, List<Fact>> {
		@Override
		protected List<Fact> doInBackground(Integer... params) {
			if (params.length > 0 && params[0] != null) 
			    mOffset = params[0];
			List<Fact> facts = mAutochmo._getFactList(mOffset, mLimit);
	        if (facts == null)
	        {
	            Toast.makeText(LentaActivity.this, R.string.connection_failed, Toast.LENGTH_LONG).show();
	            return null;
	        }
			return facts;
		}
    	@Override
    	protected void onPostExecute(List<Fact> result) {
    		setAdapterToList(result);
    		super.onPostExecute(result);
    	}
    }
    public SharedPreferences settings;
    FactListAdapter mAdapter;
//    public String passwordHash;
    private int mOffset = 0;
    private int mLimit;
    private AutochmoApplication mAutochmo;
    private PullToRefreshListView imageListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lenta);
        imageListView = (PullToRefreshListView)findViewById(R.id.lentaListView);
        imageListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (mAdapter != null) {
        			mAdapter.clear();
        		}
				imageListView.setLastUpdatedLabel(DateUtils.formatDateTime(mAutochmo,
						System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL));
        		new GetFactTask().execute(0);
			}
		});
        imageListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
        	@Override
        	public void onLastItemVisible() {
        		new GetFactTask().execute(mOffset + mLimit);
        	}
		});
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mAutochmo = (AutochmoApplication)getApplication();
        mLimit = Integer.valueOf(settings.getString(getString(R.string.factCountKey), "30"));
        imageListView.getRefreshableView().setOnItemClickListener(itemClickListener);
        // Получить список фактов
        imageListView.setRefreshing(true);
        new GetFactTask().execute(0);
    }
    public void setAdapterToList(List<Fact> facts) {
    	if (mAdapter == null) {
			mAdapter = new FactListAdapter(LentaActivity.this, facts, imageListView.getRefreshableView());
			imageListView.getRefreshableView().setAdapter(mAdapter);
		} else {
			for (Fact f : facts) {
				mAdapter.add(f);
			}
//			mAdapter.notifyDataSetChanged();
		}
    	imageListView.onRefreshComplete();
    }
    OnItemClickListener itemClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView listView, View view, int pos, long id) {
            Fact f = mAdapter.getItem(pos - 1);
            Intent intent = new Intent();
            intent.setClass(LentaActivity.this, FactActivity.class);
            intent.putExtra(FactActivity.FACT, f);
            startActivity(intent);
        }
    };
}