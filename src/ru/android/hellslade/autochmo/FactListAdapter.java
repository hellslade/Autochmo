package ru.android.hellslade.autochmo;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FactListAdapter extends ArrayAdapter<Fact> {
	private AutochmoApplication mAutochmo;
	private ListView listView;
	
    public FactListAdapter(Activity activity, List<Fact> facts, ListView listView) {
        super(activity, 0, facts);
        this.listView = listView;
        mAutochmo = (AutochmoApplication)activity.getApplication();
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity activity = (Activity) getContext();
     // Inflate the views from XML
        View rowView = convertView;
        ViewCache viewCache;
        if (rowView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.fact_list_row, null);
            viewCache = new ViewCache(rowView);
            rowView.setTag(viewCache);
        } else {
            viewCache = (ViewCache) rowView.getTag();
        }
        Fact fact = getItem(position);
 
        // Load the image and set it on the ImageView
        ImageView imageView = viewCache.getImageView();
    	String imageUrl = activity.getString(R.string.host_image)+fact.getPictureSmall(0);
    	mAutochmo.imageLoader.displayImage(imageUrl, imageView);

        // Set the text on the TextView
        //TextView factNomerView = viewCache.getfactNomerView();
        //factNomerView.setText(String.format("%s", fact.getGosnomer().toUpperCase()));
        TextView nomerView = viewCache.getNomerView();
        TextView regionView = viewCache.getRegionView();
        int[] i = getNomerShape(fact.getGosnomerType());
        nomerView.setBackgroundResource(i[0]);
        regionView.setBackgroundResource(i[1]);
        nomerView.setText(fact.getGosnomerNomer().toUpperCase());
        regionView.setText(fact.getGosnomerRegion());
        
        TextView factDateView = viewCache.getfactDateView();
        SimpleDateFormat sdf_parse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
			Date date = sdf_parse.parse(fact.getDatecreatedStr());
			factDateView.setText(DateFormat.getLongDateFormat(activity).format(date));
		} catch (ParseException e) {
			factDateView.setText(fact.getDatecreatedStr().split(" ")[0]);
		}
        
        TextView factCarmodelView = viewCache.getfactCarmodelView();
        factCarmodelView.setText(String.format("%s %s", fact.getCarModelManufacturer(), fact.getCarModel()));
        
        /*TextView factCommentView = viewCache.getfactCommentView();
        //factCommentView.setText(fact.getComment());
        factCommentView.setText("");
        //factCommentView.setMovementMethod(new ScrollingMovementMethod());
        */
        TextView ratingPlusView = viewCache.getratingPlusView();
        /*Drawable img_p = getContext().getResources().getDrawable(R.drawable.rate_p);
        img_p.setBounds( 0, 0, 15, 15 );
        ratingPlusView.setCompoundDrawables(img_p, null, null, null);*/
        ratingPlusView.setText(fact.getRatingPlus());
        
        TextView ratingMinusView = viewCache.getratingMinusView();
        /*Drawable img_m = getContext().getResources().getDrawable(R.drawable.rate_m);
        img_m.setBounds( 0, 0, 15, 15 );
        ratingMinusView.setCompoundDrawables(img_m, null, null, null);*/
        ratingMinusView.setText(fact.getRatingMinus());

        return rowView;
    }
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
}