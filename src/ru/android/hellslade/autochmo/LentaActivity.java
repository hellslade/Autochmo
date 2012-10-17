package ru.android.hellslade.autochmo;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

@TargetApi(11)
public class LentaActivity extends SherlockFragmentActivity {
    class GetFactTask extends AsyncTask<Integer, Void, List<Fact>> {
		@Override
		protected List<Fact> doInBackground(Integer... params) {
			if (params.length > 0 && params[0] != null) 
			    mOffset = params[0];
			List<Fact> facts = mAutochmo._getFactList(mOffset, mLimit, mGosnomer);
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
    private String mGosnomer = "";
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
        imageListView.setRefreshing(true);
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          mGosnomer = intent.getStringExtra(SearchManager.QUERY);
          setTitle(getResources().getString(R.string.app_name) + ": " + mGosnomer);
/*          int currentapiVersion = android.os.Build.VERSION.SDK_INT;
          if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
        	  getActionBar().setSubtitle("Поиск: " + mGosnomer);
          } else {
        	  setTitle(getResources().getString(R.string.app_name) + ": " + mGosnomer);
          }*/
          
          //Создаем экземпляр SearchRecentSuggestions
          SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
          //Сохраняем запрос
          suggestions.saveRecentQuery(mGosnomer, null); 
        }	
        // Получить список фактов
    	new GetFactTask().execute(0);
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
	    MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_lenta, menu);
        MenuItem searchMenu = menu.findItem(R.id.itemSearch);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        if (null!=searchManager) {
        	searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        //searchView.setIconifiedByDefault(false);
        return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
            case (R.id.itemSearch):
            	//onSearchRequested();
                break;
        }
        return super.onOptionsItemSelected(item);
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