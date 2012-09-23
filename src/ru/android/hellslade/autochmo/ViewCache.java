package ru.android.hellslade.autochmo;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {
	 
    private View baseView;
    private ImageView imageView;
    //private TextView factNomerView;
    private TextView factDateView;
    private TextView factCarmodelView;
    //private TextView factCommentView;
    private TextView ratingPlusView;
    private TextView ratingMinusView;
    private TextView nomerView;
    private TextView regionView;

    public ViewCache(View baseView) {
        this.baseView = baseView;
    }
 
    public ImageView getImageView() {
        if (imageView == null) {
            imageView = (ImageView) baseView.findViewById(R.id.factImage);
        }
        return imageView;
    }
    public TextView getNomerView() {
        if (nomerView == null) {
            nomerView = (TextView) baseView.findViewById(R.id.nomerView);
        }
        return nomerView;
    }
    public TextView getRegionView() {
        if (regionView == null) {
            regionView = (TextView) baseView.findViewById(R.id.regionView);
        }
        return regionView;
    }
    /*public TextView getfactNomerView() {
        if (factNomerView == null) {
        	factNomerView = (TextView) baseView.findViewById(R.id.factNomer);
        }
        return factNomerView;
    }*/
    public TextView getfactDateView() {
        if (factDateView == null) {
        	factDateView = (TextView) baseView.findViewById(R.id.factDate);
        }
        return factDateView;
    }
    public TextView getfactCarmodelView() {
        if (factCarmodelView == null) {
        	factCarmodelView = (TextView) baseView.findViewById(R.id.factCarmodel);
        }
        return factCarmodelView;
    }
    /*public TextView getfactCommentView() {
        if (factCommentView == null) {
        	factCommentView = (TextView) baseView.findViewById(R.id.factComment);
        }
        return factCommentView;
    }*/
    public TextView getratingPlusView() {
        if (ratingPlusView == null) {
        	ratingPlusView = (TextView) baseView.findViewById(R.id.ratingPlus);
        }
        return ratingPlusView;
    }
    public TextView getratingMinusView() {
        if (ratingMinusView == null) {
        	ratingMinusView = (TextView) baseView.findViewById(R.id.ratingMinus);
        }
        return ratingMinusView;
    }
 
}