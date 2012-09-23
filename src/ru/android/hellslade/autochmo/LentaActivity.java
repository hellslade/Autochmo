package ru.android.hellslade.autochmo;

import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.markupartist.android.widget.PullToRefreshListView;
import com.markupartist.android.widget.PullToRefreshListView.OnRefreshListener;

public class LentaActivity extends FragmentActivity {
    class GetFactTask extends AsyncTask<Integer, Integer, List<Fact>> {
    	ProgressDialog pg = new ProgressDialog(LentaActivity.this);
    	@Override
    	protected void onPreExecute() {
        	pg.setMessage(getApplicationContext().getResources().getString(R.string.get_fact_task_message));
        	pg.show();
    		super.onPreExecute();
    	}
		@Override
		protected List<Fact> doInBackground(Integer... params) {
			final int count = Integer.valueOf(settings.getString(getString(R.string.factCountKey), factCount_default));
	        facts = api._getFactList(offset, count);
	        if (facts == null)
	        {
	            Toast.makeText(LentaActivity.this, R.string.connection_failed, Toast.LENGTH_LONG).show();
	            return null;
	        }
	        // ================
			return facts;
	        
		}
    	@Override
    	protected void onPostExecute(List<Fact> result) {
    		setAdapterToList(result);
    		pg.dismiss();
    		super.onPostExecute(result);
    	}
    }
    private View mRefreshView;
    /** Called when the activity is first created. */
    public SharedPreferences settings;
    FactListAdapter listAdapter;
    public List<Fact> facts;
    public String passwordHash;
    private int offset;
    private String factCount_default;
    private AutochmoAPI api;
    private ListView imageListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lenta);

        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(View v) {
                // Do work to refresh the list here.
                mRefreshView = v;
                new GetDataTask().execute();
            }
        });
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        api = new AutochmoAPI(this);
        offset = 0;
        factCount_default = "30";
        imageListView = getListView();
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        imageListView.setOnItemClickListener(itemClickListener);
        // Получить список фактов
        //_getFactList(offset);
        new GetFactTask().execute(offset);
    }
    public void _getFactList(int offset)
    {
        final int count = Integer.valueOf(settings.getString(getString(R.string.factCountKey), factCount_default));
        facts = api._getFactList(offset, count);
        if (facts == null)
        {
            Toast.makeText(this, R.string.connection_failed, Toast.LENGTH_LONG).show();
            return;
        }
        // ================
        setAdapterToList(facts);
    }
    public void setAdapterToList(List<Fact> facts) {
        imageListView.setAdapter(null);
        FactListAdapter listAdapter = new FactListAdapter(LentaActivity.this, facts, imageListView);
        imageListView.setAdapter(listAdapter);
        imageListView.invalidateViews();
    }
    OnItemClickListener itemClickListener = new OnItemClickListener()
    {
        public void onItemClick(AdapterView listView, View view, int pos, long id) {
            Fact f = facts.get(pos - 1);
            List<Comment> comments = api._getComment(f.getFactId());
            Intent intent = new Intent();
            intent.setClass(LentaActivity.this, FactActivity.class);
            intent.putExtra(FactActivity.FACT, f);
            intent.putExtra(FactActivity.COUNT, comments.size());
            for (int i = 0; i < comments.size(); i++)
            {
                intent.putExtra(FactActivity.COMMENT+i, comments.get(i));
            }
            startActivity(intent);
        }

    };
    private class GetDataTask extends AsyncTask<Void, Void, List<Fact>> {

        @Override
        protected List<Fact> doInBackground(Void... params) {
            // Simulates a background job.
            try {
                final int count = Integer.valueOf(settings.getString(getString(R.string.factCountKey), factCount_default));
                if (mRefreshView.getId() == R.id.pull_to_refresh_footer) {
                    offset += count;
                } else {
                    offset -= count;
                }
                offset = offset >= 0 ? offset : 0;
                facts = api._getFactList(offset, count);
            } catch (Exception e) {
                ;
            }
            return facts;
        }
        
        @Override
        protected void onPostExecute(List<Fact> result) {
            //mListItems.addFirst("Added after refresh...");
            facts = result;
            setAdapterToList(facts);
            // Call onRefreshComplete when the list has been refreshed.
            ((PullToRefreshListView) getListView()).onRefreshComplete(mRefreshView);

            super.onPostExecute(result);
        }
    }
    public PullToRefreshListView getListView() {
        PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.lentaListView);
        return list;
    }
}