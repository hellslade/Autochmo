package ru.android.hellslade.autochmo;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CommentListAdapter  extends ArrayAdapter<Comment> {
//	private AsyncImageLoader asyncImageLoader;
	private AutochmoApplication mAutochmo;
	private ListView listView;
	
    public CommentListAdapter(Activity activity, List<Comment> comments, ListView listView) {
        super(activity, 0, comments);
        this.listView = listView;
//        asyncImageLoader = new AsyncImageLoader(activity);
        mAutochmo = (AutochmoApplication)activity.getApplication();
    }
    ExpandablePanel.OnExpandListener listener = new ExpandablePanel.OnExpandListener() {
        @Override
        public void onExpand(View handle, View content) {
            Button btn = (Button) handle;
            btn.setText("<");
        }
        @Override
        public void onCollapse(View handle, View content) {
            Button btn = (Button) handle;
            btn.setText(">");
        }
    };
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
     // Inflate the views from XML
        LayoutInflater inflater = activity.getLayoutInflater();
        convertView = inflater.inflate(R.layout.comment_list_row, null);
        
        ExpandablePanel panel = (ExpandablePanel) convertView.findViewById(R.id.foo);
        panel.setOnExpandListener(listener);

        Comment comment = getItem(position);
        // Load the image and set it on the ImageView
        String imageUrl = activity.getString(R.string.host_image)+comment.getUserPicture();
        ImageView imageView = (ImageView) convertView.findViewById(R.id.userPicture);
        mAutochmo.imageLoader.displayImage(imageUrl, imageView);
        // Set the text on the TextView
        TextView userComment = (TextView)convertView.findViewById(R.id.userComment);
        userComment.setText(comment.getText());

        TextView userLogin = (TextView)convertView.findViewById(R.id.userLogin);
        String username = comment.getUserFullname().trim();
        if (TextUtils.isEmpty(username))
        { // Если имя пользователя неизвестно, то покажем его логин
        	username = comment.getUserlogin();
        }
        userLogin.setText(username);
        
        TextView userDate = (TextView)convertView.findViewById(R.id.userDate);
        SimpleDateFormat sdf_parse = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
        try {
			Date date = sdf_parse.parse(comment.getDatecreatedStr());
			userDate.setText(DateFormat.getLongDateFormat(activity).format(date) + " " + DateFormat.getTimeFormat(activity).format(date));
		} catch (ParseException e) {
			Log.v(e.getMessage());
			userDate.setText(comment.getDatecreatedStr());
		}
        
 
        return convertView;
    }
}