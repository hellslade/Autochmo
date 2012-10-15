package ru.android.hellslade.autochmo;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class CommentAddActivity extends SherlockFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcomment);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_add_comment, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.itemOk):
                EditText comment = (EditText)findViewById(R.id.newCommentEditText);
                String comment_str = comment.getText().toString().trim();
                if (comment_str.isEmpty()) {
                    Toast.makeText(this, "Нельзя добавлять пустой комментарий", Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intent = this.getIntent();
                intent.putExtra("comment", comment_str);
                setResult(RESULT_OK, intent);
                this.finish();
                break;
            case (R.id.itemCancel):
                setResult(RESULT_CANCELED);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
