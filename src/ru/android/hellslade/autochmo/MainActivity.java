package ru.android.hellslade.autochmo;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends SherlockFragmentActivity {
    private Context mContext;
    private AutochmoApplication mAutochmo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAutochmo = (AutochmoApplication)getApplication();
        setContentView(R.layout.main);
        mContext = this;
        bindButtons();
    }
    
    private void bindButtons() {
        Button btnNewMessage = (Button) findViewById(R.id.btn_lenta);
        btnNewMessage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!mAutochmo.isOnline())
                {
                    Toast.makeText(getApplicationContext(), R.string.connection_failed, Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent();
                intent.setClass(mContext, LentaActivity.class);
                startActivity(intent);
            }
        });

        Button btnMessages = (Button) findViewById(R.id.btn_add_fact);
        btnMessages.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, FactAddActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemExit):
                this.finish();
                break;
            case (R.id.itemSettings):
                // Launch Preference activity
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}